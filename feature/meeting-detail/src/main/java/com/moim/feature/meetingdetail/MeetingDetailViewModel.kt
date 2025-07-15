package com.moim.feature.meetingdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.moim.core.common.delegate.MeetingAction
import com.moim.core.common.delegate.MeetingViewModelDelegate
import com.moim.core.common.delegate.PlanAction
import com.moim.core.common.delegate.PlanItemViewModelDelegate
import com.moim.core.common.delegate.meetingStateIn
import com.moim.core.common.delegate.planItemStateIn
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.util.parseZonedDateTime
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.ToastMessage
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.common.view.checkState
import com.moim.core.common.view.restartableStateIn
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.model.Meeting
import com.moim.core.model.Plan
import com.moim.core.model.Review
import com.moim.core.model.item.asPlan
import com.moim.core.model.item.asPlanItem
import com.moim.core.model.item.asReview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class MeetingDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val planRepository: PlanRepository,
    private val meetingRepository: MeetingRepository,
    reviewRepository: ReviewRepository,
    meetingViewModelDelegate: MeetingViewModelDelegate,
    planItemViewModelDelegate: PlanItemViewModelDelegate
) : BaseViewModel(),
    MeetingViewModelDelegate by meetingViewModelDelegate,
    PlanItemViewModelDelegate by planItemViewModelDelegate {

    private val meetingId
        get() = savedStateHandle.get<String>(KEY_MEETING_ID) ?: ""

    private val meetingActionReceiver = meetingAction.meetingStateIn(viewModelScope)
    private val planActionReceiver = planItemAction.planItemStateIn(viewModelScope)

    private val meetingDetailResult =
        combine(
            meetingRepository.getMeeting(meetingId),
            planRepository.getPlans(meetingId),
            reviewRepository.getReviews(meetingId),
            ::Triple
        ).asResult().restartableStateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            launch {
                meetingDetailResult.collect { result ->
                    when (result) {
                        is Result.Loading -> setUiState(MeetingDetailUiState.Loading)
                        is Result.Success -> {
                            val user = userRepository.getUser().first()
                            val (meeting, plans, reviews) = result.data

                            setUiState(
                                MeetingDetailUiState.Success(
                                    userId = user.userId,
                                    meeting = meeting,
                                    plans = plans.sortedByDescending { it.planAt },
                                    reviews = reviews.sortedByDescending { it.reviewAt },
                                )
                            )
                        }

                        is Result.Error -> setUiState(MeetingDetailUiState.Error)
                    }
                }
            }

            launch {
                meetingActionReceiver.collect { action ->
                    uiState.checkState<MeetingDetailUiState.Success> {
                        when (action) {
                            is MeetingAction.MeetingUpdate -> setUiState(copy(meeting = action.meeting))
                            is MeetingAction.MeetingInvalidate -> meetingDetailResult.restart()
                            else -> return@collect
                        }
                    }
                }
            }

            launch {
                planActionReceiver.collect { action ->
                    uiState.checkState<MeetingDetailUiState.Success> {
                        when (action) {
                            is PlanAction.PlanCreate -> {
                                val plans = plans.toMutableList()
                                    .apply {
                                        withIndex()
                                            .firstOrNull {
                                                val newPlanTime = action.planItem.planAt.parseZonedDateTime()
                                                val currentPlanTime = it.value.planAt.parseZonedDateTime()
                                                newPlanTime.isBefore(currentPlanTime)
                                            }
                                            ?.let { add(it.index, action.planItem.asPlan()) }
                                            ?: run { add(action.planItem.asPlan()) }
                                    }

                                setUiState(copy(plans = plans))
                            }

                            is PlanAction.PlanUpdate -> {
                                if (action.planItem.isPlanAtBefore) {
                                    val plans = plans.toMutableList().apply {
                                        val index = withIndex()
                                            .find { it.value.planId == action.planItem.postId }
                                            ?.index
                                            ?: return@collect


                                        set(index, action.planItem.asPlan())
                                    }

                                    setUiState(copy(plans = plans))
                                } else {
                                    val reviews = reviews.toMutableList().apply {
                                        val index = withIndex()
                                            .find { it.value.reviewId == action.planItem.postId }
                                            ?.index
                                            ?: return@collect

                                        set(index, action.planItem.asReview())
                                    }

                                    setUiState(copy(reviews = reviews))
                                }
                            }

                            is PlanAction.PlanDelete -> setUiState(
                                copy(
                                    plans = plans.toMutableList().apply {
                                        val removePlan = find { it.planId == action.postId } ?: return@apply
                                        remove(removePlan)
                                    },
                                    reviews = reviews.toMutableList().apply {
                                        val removeReview = find { it.reviewId == action.postId } ?: return@apply
                                        remove(removeReview)
                                    },
                                )
                            )

                            is PlanAction.PlanInvalidate -> meetingDetailResult.restart()

                            is PlanAction.None -> return@collect
                        }
                    }
                }
            }
        }
    }

    fun onUiAction(uiAction: MeetingDetailUiAction) {
        when (uiAction) {
            is MeetingDetailUiAction.OnClickBack -> setUiEvent(MeetingDetailUiEvent.NavigateToBack)
            is MeetingDetailUiAction.OnClickRefresh -> meetingDetailResult.restart()
            is MeetingDetailUiAction.OnClickPlanWrite -> navigateToPlanWrite()
            is MeetingDetailUiAction.OnClickMeetingSetting -> navigateToMeetingSetting()
            is MeetingDetailUiAction.OnClickPlanTab -> setPlanTab(uiAction.isBefore)
            is MeetingDetailUiAction.OnClickPlanApply -> setPlanApply(uiAction.planId, uiAction.isApply)
            is MeetingDetailUiAction.OnClickPlanDetail -> setUiEvent(MeetingDetailUiEvent.NavigateToPlanDetail(uiAction.postId, uiAction.isPlan))
            is MeetingDetailUiAction.OnClickMeetingImage -> setUiEvent(MeetingDetailUiEvent.NavigateToImageViewer(uiAction.imageUrl, uiAction.meetingName))
            is MeetingDetailUiAction.OnClickMeetingInvite -> getInviteLink()
            is MeetingDetailUiAction.OnShowPlanApplyCancelDialog -> showApplyCancelDialog(uiAction.isShow, uiAction.cancelPlanId)
        }
    }

    private fun setPlanApply(
        planId: String,
        isApply: Boolean
    ) {
        viewModelScope.launch {
            if (isApply) {
                planRepository.joinPlan(planId)
            } else {
                planRepository.leavePlan(planId)
            }.asResult().onEach { setLoading(it is Result.Loading) }.collect { result ->
                uiState.checkState<MeetingDetailUiState.Success> {
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> {
                            val plan = plans.first { plan -> plan.planId == planId }
                            updatePlanItem(ZonedDateTime.now(), plan.copy(isParticipant = !plan.isParticipant).asPlanItem())

                            if (isApply.not()) {
                                setUiState(copy(cancelPlanId = null, isShowApplyCancelDialog = false))
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


    private fun showApplyCancelDialog(isShow: Boolean, cancelPlanId: String?) {
        uiState.checkState<MeetingDetailUiState.Success> {
            setUiState(
                copy(
                    isShowApplyCancelDialog = isShow,
                    cancelPlanId = cancelPlanId
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
                        planAt = ZonedDateTime.now().toString()
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

    companion object {
        private const val KEY_MEETING_ID = "meetingId"
    }
}


sealed interface MeetingDetailUiState : UiState {
    data object Loading : MeetingDetailUiState

    data class Success(
        val userId: String = "",
        val meeting: Meeting = Meeting(),
        val plans: List<Plan> = emptyList(),
        val reviews: List<Review> = emptyList(),
        val cancelPlanId: String? = null,
        val isShowApplyCancelDialog: Boolean = false,
        val isPlanSelected: Boolean = true,
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
        val planId: String,
        val isApply: Boolean
    ) : MeetingDetailUiAction

    data class OnClickPlanDetail(
        val postId: String,
        val isPlan: Boolean
    ) : MeetingDetailUiAction

    data class OnClickMeetingImage(
        val imageUrl: String,
        val meetingName: String
    ) : MeetingDetailUiAction

    data class OnShowPlanApplyCancelDialog(
        val isShow: Boolean,
        val cancelPlanId: String?,
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
        val postId: String,
        val isPlan: Boolean
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