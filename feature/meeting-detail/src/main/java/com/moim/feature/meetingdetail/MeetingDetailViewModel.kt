package com.moim.feature.meetingdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertSeparators
import androidx.paging.map
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.model.Meeting
import com.moim.core.common.model.Plan
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.model.item.PlanItem
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.domain.usecase.GetPlanItemsUseCase
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.MeetingAction
import com.moim.core.ui.eventbus.PlanAction
import com.moim.core.ui.eventbus.actionStateIn
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.ToastMessage
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import com.moim.core.ui.view.checkedActionedAtIsBeforeLoadedAt
import com.moim.core.ui.view.restartableStateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class MeetingDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getPlanItemsUseCase: GetPlanItemsUseCase,
    private val planRepository: PlanRepository,
    private val reviewRepository: ReviewRepository,
    private val meetingRepository: MeetingRepository,
    userRepository: UserRepository,
    meetingEventBus: EventBus<MeetingAction>,
    private val planEventBus: EventBus<PlanAction>
) : BaseViewModel() {

    private val meetingId
        get() = savedStateHandle.get<String>(KEY_MEETING_ID) ?: ""

    private val meetingActionReceiver = meetingEventBus
        .action
        .actionStateIn(viewModelScope, MeetingAction.None)
    private val planActionReceiver = planEventBus
        .action
        .actionStateIn(viewModelScope, PlanAction.None)

    private val pagingRefreshSignal = MutableSharedFlow<Unit>()
    private val loadDataSignal: Flow<Unit> = flow {
        emit(Unit)
        emitAll(pagingRefreshSignal)
    }

    private var _plans = loadDataSignal
        .flatMapLatest { getPlanItemsUseCase(GetPlanItemsUseCase.Params(meetId = meetingId, isPlanAtBefore = true)) }
        .cachedIn(viewModelScope)
    private val plans = planActionReceiver.flatMapLatest { receiver ->
        when (receiver) {
            is PlanAction.None -> _plans

            is PlanAction.PlanCreate -> {
                _plans.map { pagingData ->
                    pagingData.checkedActionedAtIsBeforeLoadedAt(
                        actionedAt = receiver.actionAt,
                        loadedAt = getPlanItemsUseCase.loadedAt
                    ) {
                        pagingData.insertSeparators { before: PlanItem?, aftet: PlanItem? ->
                            if (before == null) {
                                return@insertSeparators receiver.planItem
                            } else {
                                null
                            }
                        }
                    }
                }
            }

            is PlanAction.PlanUpdate -> {
                _plans.map { pagingData ->
                    pagingData.checkedActionedAtIsBeforeLoadedAt(
                        actionedAt = receiver.actionAt,
                        loadedAt = getPlanItemsUseCase.loadedAt
                    ) {
                        if (receiver.planItem.isPlanAtBefore.not()) {
                            pagingData
                        } else {
                            pagingData.map { planItem ->
                                if (planItem.postId == receiver.planItem.postId) {
                                    receiver.planItem
                                } else {
                                    planItem
                                }
                            }
                        }
                    }
                }
            }

            is PlanAction.PlanDelete -> {
                _plans.map { pagingData ->
                    pagingData.checkedActionedAtIsBeforeLoadedAt(
                        actionedAt = receiver.actionAt,
                        loadedAt = getPlanItemsUseCase.loadedAt
                    ) {
                        pagingData
                            .map { planItem ->
                                if (planItem.postId == receiver.postId) {
                                    planItem.apply { isDeleted = true }
                                } else {
                                    planItem
                                }
                            }.filter { it.isDeleted.not() }
                    }
                }
            }

            is PlanAction.PlanInvalidate -> {
                onRefresh()
                _plans
            }
        }.also {
            _plans = it
        }
    }.cachedIn(viewModelScope)

    private var _review = loadDataSignal
        .flatMapLatest { getPlanItemsUseCase(GetPlanItemsUseCase.Params(meetId = meetingId, isPlanAtBefore = false)) }
        .cachedIn(viewModelScope)
    private val reviews = planActionReceiver.flatMapLatest { receiver ->
        when (receiver) {
            is PlanAction.None,
            is PlanAction.PlanCreate,
            is PlanAction.PlanInvalidate -> _review

            is PlanAction.PlanUpdate -> _review.map { pagingData ->
                pagingData.checkedActionedAtIsBeforeLoadedAt(
                    actionedAt = receiver.actionAt,
                    loadedAt = getPlanItemsUseCase.loadedAt
                ) {
                    if (receiver.planItem.isPlanAtBefore) {
                        pagingData
                    } else {
                        pagingData.map { planItem ->
                            if (planItem.postId == receiver.planItem.postId) {
                                receiver.planItem
                            } else {
                                planItem
                            }
                        }
                    }
                }
            }

            is PlanAction.PlanDelete -> _review.map { pagingData ->
                pagingData.checkedActionedAtIsBeforeLoadedAt(
                    actionedAt = receiver.actionAt,
                    loadedAt = getPlanItemsUseCase.loadedAt
                ) {
                    pagingData
                        .map { planItem ->
                            if (planItem.postId == receiver.postId) {
                                planItem.apply { isDeleted = true }
                            } else {
                                planItem
                            }
                        }.filter { it.isDeleted.not() }
                }
            }
        }.also {
            _review = it
        }
    }.cachedIn(viewModelScope)

    private val meetingDetailUiState =
        combine(
            userRepository.getUser(),
            meetingRepository.getMeeting(meetingId),
            getPlanAndReviewTotalCount(),
            ::Triple
        )
            .asResult()
            .mapLatest { result ->
                when (result) {
                    is Result.Loading -> MeetingDetailUiState.Loading

                    is Result.Success -> {
                        val (user, meeting, counts) = result.data
                        val (planCount, reviewCount) = counts

                        MeetingDetailUiState.Success(
                            userId = user.userId,
                            meeting = meeting,
                            planTotalCount = planCount,
                            reviewTotalCount = reviewCount,
                        )
                    }

                    is Result.Error -> MeetingDetailUiState.Error
                }
            }
            .restartableStateIn(viewModelScope, SharingStarted.Lazily, MeetingDetailUiState.Loading)

    init {
        viewModelScope.launch {
            launch {
                meetingDetailUiState.collect { uiState ->
                    if (uiState is MeetingDetailUiState.Success) {
                        setUiState(
                            uiState.copy(
                                plans = plans,
                                reviews = reviews
                            )
                        )
                    } else {
                        setUiState(uiState)
                    }
                }
            }

            launch {
                meetingActionReceiver.collect { action ->
                    uiState.checkState<MeetingDetailUiState.Success> {
                        when (action) {
                            is MeetingAction.MeetingUpdate -> setUiState(copy(meeting = action.meeting))
                            is MeetingAction.MeetingInvalidate -> meetingDetailUiState.restart()
                            else -> return@collect
                        }
                    }
                }
            }
        }
    }

    fun onUiAction(uiAction: MeetingDetailUiAction) {
        when (uiAction) {
            is MeetingDetailUiAction.OnClickBack -> setUiEvent(MeetingDetailUiEvent.NavigateToBack)
            is MeetingDetailUiAction.OnClickRefresh -> meetingDetailUiState.restart()
            is MeetingDetailUiAction.OnClickPlanWrite -> navigateToPlanWrite()
            is MeetingDetailUiAction.OnClickMeetingSetting -> navigateToMeetingSetting()
            is MeetingDetailUiAction.OnClickPlanTab -> setPlanTab(uiAction.isBefore)
            is MeetingDetailUiAction.OnClickPlanApply -> setPlanApply(uiAction.planItem, uiAction.isApply)
            is MeetingDetailUiAction.OnClickPlanDetail -> setUiEvent(MeetingDetailUiEvent.NavigateToPlanDetail(uiAction.viewIdType))
            is MeetingDetailUiAction.OnClickMeetingImage -> setUiEvent(MeetingDetailUiEvent.NavigateToImageViewer(uiAction.imageUrl, uiAction.meetingName))
            is MeetingDetailUiAction.OnClickMeetingInvite -> getInviteLink()
            is MeetingDetailUiAction.OnShowPlanApplyCancelDialog -> showApplyCancelDialog(uiAction.isShow, uiAction.cancelPlanItem)
        }
    }

    private fun getPlanAndReviewTotalCount() = flow {
        coroutineScope {
            val plans = async { planRepository.getPlans(meetingId, "", 1) }
            val reviews = async { reviewRepository.getReviews(meetingId, "", 1) }
            emit(plans.await().totalCount to reviews.await().totalCount)
        }
    }

    private fun setPlanApply(
        planItem: PlanItem,
        isApply: Boolean
    ) {
        viewModelScope.launch {
            if (isApply) {
                planRepository.joinPlan(planItem.postId)
            } else {
                planRepository.leavePlan(planItem.postId)
            }.asResult().onEach { setLoading(it is Result.Loading) }.collect { result ->
                uiState.checkState<MeetingDetailUiState.Success> {
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> {
                            planEventBus.send(PlanAction.PlanUpdate(planItem = planItem.copy(isParticipant = !planItem.isParticipant)))
                            if (isApply.not()) {
                                setUiState(copy(cancelPlanItem = null, isShowApplyCancelDialog = false))
                            }
                        }

                        is Result.Error -> showErrorMessage(result.exception)
                    }
                }
            }
        }
    }

    private fun setPlanTab(isBefore: Boolean) {
        uiState.checkState<MeetingDetailUiState.Success> {
            if (isPlanSelected == isBefore) return@checkState
            setUiState(copy(isPlanSelected = isBefore))
        }
    }

    private fun getInviteLink() {
        viewModelScope.launch {
            uiState.checkState<MeetingDetailUiState.Success> {
                meetingRepository.getMeetingInviteCode(meeting.id)
                    .asResult()
                    .onEach { setLoading(it is Result.Loading) }
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> return@collect
                            is Result.Success -> setUiEvent(MeetingDetailUiEvent.NavigateToExternalShareUrl(result.data))
                            is Result.Error -> showErrorMessage(result.exception)
                        }
                    }
            }
        }
    }

    private fun showApplyCancelDialog(isShow: Boolean, cancelPlanItem: PlanItem?) {
        uiState.checkState<MeetingDetailUiState.Success> {
            setUiState(
                copy(
                    isShowApplyCancelDialog = isShow,
                    cancelPlanItem = cancelPlanItem
                )
            )
        }
    }

    private fun showErrorMessage(error: Throwable) {
        when (error) {
            is IOException -> setUiEvent(MeetingDetailUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
            is NetworkException -> setUiEvent(MeetingDetailUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
        }
    }

    private fun navigateToPlanWrite() {
        uiState.checkState<MeetingDetailUiState.Success> {
            setUiEvent(
                MeetingDetailUiEvent.NavigateToPlanWrite(
                    Plan(
                        meetingId = meeting.id,
                        meetingName = meeting.name,
                        meetingImageUrl = meeting.imageUrl,
                        planAt = ZonedDateTime.now()
                    )
                )
            )
        }
    }

    private fun navigateToMeetingSetting() {
        uiState.checkState<MeetingDetailUiState.Success> {
            setUiEvent(MeetingDetailUiEvent.NavigateToMeetingSetting(meeting))
        }
    }

    private fun onRefresh() {
        viewModelScope.launch {
            pagingRefreshSignal.emit(Unit)
        }
    }

    companion object {
        private const val KEY_MEETING_ID = "meetingId"
    }
}


sealed interface MeetingDetailUiState : UiState {
    data object Loading : MeetingDetailUiState

    data class Success(
        val userId: String = "",
        val meeting: Meeting = Meeting(),
        val isShowApplyCancelDialog: Boolean = false,
        val isPlanSelected: Boolean = true,
        val cancelPlanItem: PlanItem? = null,
        val plans: Flow<PagingData<PlanItem>>? = null,
        val reviews: Flow<PagingData<PlanItem>>? = null,
        val planTotalCount: Int = 0,
        val reviewTotalCount: Int = 0,
    ) : MeetingDetailUiState

    data object Error : MeetingDetailUiState
}

sealed interface MeetingDetailUiAction : UiAction {
    data object OnClickBack : MeetingDetailUiAction

    data object OnClickRefresh : MeetingDetailUiAction

    data object OnClickPlanWrite : MeetingDetailUiAction

    data object OnClickMeetingSetting : MeetingDetailUiAction

    data object OnClickMeetingInvite : MeetingDetailUiAction

    data class OnClickPlanTab(
        val isBefore: Boolean
    ) : MeetingDetailUiAction

    data class OnClickPlanApply(
        val planItem: PlanItem,
        val isApply: Boolean
    ) : MeetingDetailUiAction

    data class OnClickPlanDetail(
        val viewIdType: ViewIdType,
    ) : MeetingDetailUiAction

    data class OnClickMeetingImage(
        val imageUrl: String,
        val meetingName: String
    ) : MeetingDetailUiAction

    data class OnShowPlanApplyCancelDialog(
        val isShow: Boolean,
        val cancelPlanItem: PlanItem?,
    ) : MeetingDetailUiAction
}

sealed interface MeetingDetailUiEvent : UiEvent {
    data object NavigateToBack : MeetingDetailUiEvent

    data class NavigateToPlanWrite(
        val plan: Plan
    ) : MeetingDetailUiEvent

    data class NavigateToMeetingSetting(
        val meeting: Meeting
    ) : MeetingDetailUiEvent

    data class NavigateToPlanDetail(
        val viewIdType: ViewIdType,
    ) : MeetingDetailUiEvent

    data class NavigateToImageViewer(
        val imageUrl: String,
        val meetingName: String
    ) : MeetingDetailUiEvent

    data class NavigateToExternalShareUrl(
        val url: String
    ) : MeetingDetailUiEvent

    data class ShowToastMessage(
        val message: ToastMessage
    ) : MeetingDetailUiEvent
}