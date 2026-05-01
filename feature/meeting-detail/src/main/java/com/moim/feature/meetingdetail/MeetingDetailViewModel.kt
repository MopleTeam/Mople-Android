package com.moim.feature.meetingdetail

import androidx.lifecycle.viewModelScope
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.model.Meeting
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.Plan
import com.moim.core.common.model.Review
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.model.item.PlanItem
import com.moim.core.common.model.item.asPlanItem
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.MeetingAction
import com.moim.core.ui.eventbus.PlanAction
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.util.isActiveCheck
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.PagingHelper
import com.moim.core.ui.view.PagingUiState
import com.moim.core.ui.view.ToastMessage
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.ZonedDateTime

@HiltViewModel(assistedFactory = MeetingDetailViewModel.Factory::class)
class MeetingDetailViewModel @AssistedInject constructor(
    private val planRepository: PlanRepository,
    private val reviewRepository: ReviewRepository,
    private val meetingRepository: MeetingRepository,
    private val userRepository: UserRepository,
    private val meetingEventBus: EventBus<MeetingAction>,
    private val planEventBus: EventBus<PlanAction>,
    @Assisted val meetingDetailRoute: DetailRoute.MeetingDetail,
) : BaseViewModel() {
    private val meetingId = meetingDetailRoute.meetingId
    private var plansPagingJob: Job? = null
    private var reviewsPagingJob: Job? = null

    init {
        viewModelScope.launch {
            launch { loadInitial() }

            launch {
                meetingEventBus.action.collect { action ->
                    when (action) {
                        is MeetingAction.MeetingUpdate -> {
                            uiState.checkState<MeetingDetailUiState.Success> {
                                setUiState(copy(meeting = action.meeting))
                            }
                        }

                        is MeetingAction.MeetingInvalidate -> {
                            loadInitial()
                        }

                        else -> {
                            return@collect
                        }
                    }
                }
            }

            launch {
                planEventBus.action.collect { action ->
                    when (action) {
                        is PlanAction.PlanCreate -> {
                            applyPlanCreate(action.planItem)
                        }

                        is PlanAction.PlanUpdate -> {
                            applyPlanUpdate(action.planItem)
                        }

                        is PlanAction.PlanDelete -> {
                            applyPlanDelete(action.postId)
                        }

                        is PlanAction.PlanInvalidate -> {
                            getPlans()
                            getReviews()
                        }

                        is PlanAction.None -> {
                            Unit
                        }
                    }
                }
            }
        }
    }

    fun onUiAction(uiAction: MeetingDetailUiAction) {
        when (uiAction) {
            is MeetingDetailUiAction.OnClickBack -> {
                setUiEvent(MeetingDetailUiEvent.NavigateToBack)
            }

            is MeetingDetailUiAction.OnClickRefresh -> {
                loadInitial()
            }

            is MeetingDetailUiAction.OnClickPlanWrite -> {
                navigateToPlanWrite()
            }

            is MeetingDetailUiAction.OnClickMeetingSetting -> {
                navigateToMeetingSetting()
            }

            is MeetingDetailUiAction.OnClickPlanTab -> {
                setPlanTab(uiAction.isBefore)
            }

            is MeetingDetailUiAction.OnClickPlanApply -> {
                setPlanApply(uiAction.planItem, uiAction.isApply)
            }

            is MeetingDetailUiAction.OnClickPlanDetail -> {
                setUiEvent(MeetingDetailUiEvent.NavigateToPlanDetail(uiAction.viewIdType))
            }

            is MeetingDetailUiAction.OnClickMeetingImage -> {
                setUiEvent(
                    MeetingDetailUiEvent.NavigateToImageViewer(uiAction.imageUrl, uiAction.meetingName),
                )
            }

            is MeetingDetailUiAction.OnClickMeetingInvite -> {
                getInviteLink()
            }

            is MeetingDetailUiAction.OnLoadNextPage -> {
                val current = uiState.value as? MeetingDetailUiState.Success ?: return
                if (current.isPlanSelected) {
                    getPlans(current.plansPagingInfo.nextCursor)
                } else {
                    getReviews(current.reviewsPagingInfo.nextCursor)
                }
            }

            is MeetingDetailUiAction.OnShowPlanApplyCancelDialog -> {
                showApplyCancelDialog(uiAction.isShow, uiAction.cancelPlanItem)
            }
        }
    }

    private fun loadInitial() {
        viewModelScope.launch {
            setUiState(MeetingDetailUiState.Loading)
            runCatching {
                coroutineScope {
                    val userDeferred = async { userRepository.getUser().first() }
                    val meetingDeferred = async { meetingRepository.getMeeting(meetingId).first() }
                    Pair(userDeferred.await(), meetingDeferred.await())
                }
            }.onSuccess { (user, meeting) ->
                setUiState(MeetingDetailUiState.Success(userId = user.userId, meeting = meeting))
                getPlans()
                getReviews()
            }.onFailure {
                setUiState(MeetingDetailUiState.Error)
            }
        }
    }

    private fun getPlans(cursor: String? = null) {
        if (plansPagingJob.isActiveCheck()) return
        plansPagingJob =
            viewModelScope.launch {
                handlePlansPagingData(
                    pagingInfo = null,
                    isLoading = true,
                    cursor = cursor,
                )

                val pagingInfo =
                    runCatching {
                        planRepository.getPlans(
                            meetingId = meetingId,
                            cursor = cursor ?: "",
                            size = 30,
                        )
                    }.getOrNull()

                handlePlansPagingData(
                    pagingInfo = pagingInfo,
                    isLoading = false,
                    cursor = cursor,
                )
            }
    }

    private fun getReviews(cursor: String? = null) {
        if (reviewsPagingJob.isActiveCheck()) return
        reviewsPagingJob =
            viewModelScope.launch {
                handleReviewsPagingData(
                    pagingInfo = null,
                    isLoading = true,
                    cursor = cursor,
                )

                val pagingInfo =
                    runCatching {
                        reviewRepository.getReviews(
                            meetingId = meetingId,
                            cursor = cursor ?: "",
                            size = 30,
                        )
                    }.getOrNull()

                handleReviewsPagingData(
                    pagingInfo = pagingInfo,
                    isLoading = false,
                    cursor = cursor,
                )
            }
    }

    private fun handlePlansPagingData(
        pagingInfo: PaginationContainer<List<Plan>>?,
        isLoading: Boolean,
        cursor: String?,
    ) {
        uiState.checkState<MeetingDetailUiState.Success> {
            val result =
                PagingHelper.handlePagingResult(
                    pagingData = pagingInfo,
                    isLoading = isLoading,
                    currentPagingInfo = plansPagingInfo,
                    currentItems = plans,
                    isInitialLoad = cursor == null,
                    transform = { items -> items.map(Plan::asPlanItem) },
                )

            setUiState(
                copy(
                    plansPagingInfo = result.pagingInfo,
                    plans = result.items,
                    planTotalCount = result.pagingInfo.totalCount,
                ),
            )
        }
    }

    private fun handleReviewsPagingData(
        pagingInfo: PaginationContainer<List<Review>>?,
        isLoading: Boolean,
        cursor: String?,
    ) {
        uiState.checkState<MeetingDetailUiState.Success> {
            val result =
                PagingHelper.handlePagingResult(
                    pagingData = pagingInfo,
                    isLoading = isLoading,
                    currentPagingInfo = reviewsPagingInfo,
                    currentItems = reviews,
                    isInitialLoad = cursor == null,
                    transform = { items -> items.map(Review::asPlanItem) },
                )

            setUiState(
                copy(
                    reviewsPagingInfo = result.pagingInfo,
                    reviews = result.items,
                    reviewTotalCount = result.pagingInfo.totalCount,
                ),
            )
        }
    }

    private fun applyPlanCreate(newItem: PlanItem) {
        uiState.checkState<MeetingDetailUiState.Success> {
            if (newItem.meetingId != meetingId || newItem.isPlanAtBefore.not()) return@checkState
            setUiState(
                copy(
                    plans = listOf(newItem) + plans,
                    planTotalCount = planTotalCount + 1,
                ),
            )
        }
    }

    private fun applyPlanUpdate(newItem: PlanItem) {
        uiState.checkState<MeetingDetailUiState.Success> {
            if (newItem.meetingId != meetingId) return@checkState
            if (newItem.isPlanAtBefore) {
                val updated = plans.map { if (it.postId == newItem.postId) newItem else it }
                setUiState(copy(plans = updated))
            } else {
                val updated = reviews.map { if (it.postId == newItem.postId) newItem else it }
                setUiState(copy(reviews = updated))
            }
        }
    }

    private fun applyPlanDelete(postId: String) {
        uiState.checkState<MeetingDetailUiState.Success> {
            val newPlans = plans.filterNot { it.postId == postId }
            val newReviews = reviews.filterNot { it.postId == postId }
            setUiState(
                copy(
                    plans = newPlans,
                    reviews = newReviews,
                    planTotalCount = if (newPlans.size != plans.size) (planTotalCount - 1).coerceAtLeast(0) else planTotalCount,
                    reviewTotalCount = if (newReviews.size != reviews.size) (reviewTotalCount - 1).coerceAtLeast(0) else reviewTotalCount,
                ),
            )
        }
    }

    private fun setPlanApply(
        planItem: PlanItem,
        isApply: Boolean,
    ) {
        viewModelScope.launch {
            if (isApply) {
                planRepository.joinPlan(planItem.postId)
            } else {
                planRepository.leavePlan(planItem.postId)
            }.asResult().onEach { setLoading(it is Result.Loading) }.collect { result ->
                uiState.checkState<MeetingDetailUiState.Success> {
                    when (result) {
                        is Result.Loading -> {
                            return@collect
                        }

                        is Result.Success -> {
                            planEventBus.send(PlanAction.PlanUpdate(planItem = planItem.copy(isParticipant = !planItem.isParticipant)))
                            if (isApply.not()) {
                                setUiState(copy(cancelPlanItem = null, isShowApplyCancelDialog = false))
                            }
                        }

                        is Result.Error -> {
                            showErrorMessage(result.exception)
                        }
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
                meetingRepository
                    .getMeetingInviteCode(meeting.id)
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

    private fun showApplyCancelDialog(
        isShow: Boolean,
        cancelPlanItem: PlanItem?,
    ) {
        uiState.checkState<MeetingDetailUiState.Success> {
            setUiState(
                copy(
                    isShowApplyCancelDialog = isShow,
                    cancelPlanItem = cancelPlanItem,
                ),
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
                        planAt = ZonedDateTime.now(),
                    ),
                ),
            )
        }
    }

    private fun navigateToMeetingSetting() {
        uiState.checkState<MeetingDetailUiState.Success> {
            setUiEvent(MeetingDetailUiEvent.NavigateToMeetingSetting(meeting))
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(meetingDetailRoute: DetailRoute.MeetingDetail): MeetingDetailViewModel
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
        val plans: List<PlanItem> = emptyList(),
        val reviews: List<PlanItem> = emptyList(),
        val plansPagingInfo: PagingUiState = PagingUiState(),
        val reviewsPagingInfo: PagingUiState = PagingUiState(),
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

    data object OnLoadNextPage : MeetingDetailUiAction

    data class OnClickPlanTab(
        val isBefore: Boolean,
    ) : MeetingDetailUiAction

    data class OnClickPlanApply(
        val planItem: PlanItem,
        val isApply: Boolean,
    ) : MeetingDetailUiAction

    data class OnClickPlanDetail(
        val viewIdType: ViewIdType,
    ) : MeetingDetailUiAction

    data class OnClickMeetingImage(
        val imageUrl: String,
        val meetingName: String,
    ) : MeetingDetailUiAction

    data class OnShowPlanApplyCancelDialog(
        val isShow: Boolean,
        val cancelPlanItem: PlanItem?,
    ) : MeetingDetailUiAction
}

sealed interface MeetingDetailUiEvent : UiEvent {
    data object NavigateToBack : MeetingDetailUiEvent

    data class NavigateToPlanWrite(
        val plan: Plan,
    ) : MeetingDetailUiEvent

    data class NavigateToMeetingSetting(
        val meeting: Meeting,
    ) : MeetingDetailUiEvent

    data class NavigateToPlanDetail(
        val viewIdType: ViewIdType,
    ) : MeetingDetailUiEvent

    data class NavigateToImageViewer(
        val imageUrl: String,
        val meetingName: String,
    ) : MeetingDetailUiEvent

    data class NavigateToExternalShareUrl(
        val url: String,
    ) : MeetingDetailUiEvent

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : MeetingDetailUiEvent
}
