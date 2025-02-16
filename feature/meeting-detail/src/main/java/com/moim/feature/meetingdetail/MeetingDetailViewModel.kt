package com.moim.feature.meetingdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.moim.core.common.delegate.MeetingAction
import com.moim.core.common.delegate.MeetingViewModelDelegate
import com.moim.core.common.delegate.PlanAction
import com.moim.core.common.delegate.PlanViewModelDelegate
import com.moim.core.common.delegate.meetingStateIn
import com.moim.core.common.delegate.planStateIn
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.util.parseZonedDateTimeForDateString
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.ToastMessage
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.common.view.checkState
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.model.Meeting
import com.moim.core.model.Plan
import com.moim.core.model.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class MeetingDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    meetingRepository: MeetingRepository,
    planRepository: PlanRepository,
    reviewRepository: ReviewRepository,
    meetingViewModelDelegate: MeetingViewModelDelegate,
    planViewModelDelegate: PlanViewModelDelegate
) : BaseViewModel(),
    MeetingViewModelDelegate by meetingViewModelDelegate,
    PlanViewModelDelegate by planViewModelDelegate {

    private val meetingId
        get() = savedStateHandle.get<String>(KEY_MEETING_ID) ?: ""

    private val meetingActionReceiver = meetingAction.meetingStateIn(viewModelScope)
    private val planActionReceiver = planAction.planStateIn(viewModelScope)

    private val meetingDetailResult = loadDataSignal
        .flatMapLatest {
            combine(
                meetingRepository.getMeeting(meetingId),
                planRepository.getPlans(meetingId),
                reviewRepository.getReviews(meetingId),
                ::Triple
            ).asResult()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            launch {
                meetingDetailResult.collect { result ->
                    when (result) {
                        is Result.Loading -> setUiState(MeetingDetailUiState.Loading)
                        is Result.Success -> {
                            val (meeting, plans, reviews) = result.data

                            setUiState(
                                MeetingDetailUiState.Success(
                                    meeting = meeting,
                                    plans = plans,
                                    reviews = reviews,
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
                            is MeetingAction.MeetingInvalidate -> onRefresh()
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
                                                val newPlanTime = action.plan.planTime.parseZonedDateTimeForDateString()
                                                val currentPlanTime = it.value.planTime.parseZonedDateTimeForDateString()
                                                newPlanTime.isBefore(currentPlanTime)
                                            }
                                            ?.let { add(it.index, action.plan) }
                                            ?: run { add(action.plan) }
                                    }

                                setUiState(copy(plans = plans))
                            }

                            is PlanAction.PlanUpdate -> {
                                val plans = plans.toMutableList().apply {
                                    val index = withIndex()
                                        .find { it.value.planId == action.plan.planId }
                                        ?.index
                                        ?: return@collect

                                    set(index, action.plan)
                                }

                                setUiState(copy(plans = plans))
                            }

                            is PlanAction.PlanDelete -> setUiState(
                                copy(
                                    plans = plans.toMutableList().apply {
                                        val removePlan = find { it.planId == action.planId } ?: return@collect
                                        remove(removePlan)
                                    }
                                )
                            )

                            is PlanAction.PlanInvalidate -> onRefresh()

                            is PlanAction.None -> return@collect
                        }
                    }
                }
            }
        }
    }

    fun onUiAction(uiAction: UiAction) {
        when (uiAction) {
            is MeetingDetailUiAction.OnClickBack -> setUiEvent(MeetingDetailUiEvent.NavigateToBack)
            is MeetingDetailUiAction.OnClickRefresh -> onRefresh()
            is MeetingDetailUiAction.OnClickPlanWrite -> navigateToPlanWrite()
            is MeetingDetailUiAction.OnClickMeetingSetting -> navigateToMeetingSetting()
            is MeetingDetailUiAction.OnClickPlanTab -> setPlanTab(uiAction.isBefore)
            is MeetingDetailUiAction.OnClickPlanApply -> {}
            is MeetingDetailUiAction.OnClickPlanDetail -> setUiEvent(MeetingDetailUiEvent.NavigateToPlanDetail(uiAction.postId, uiAction.isPlan))
        }
    }

    private fun setPlanTab(isBefore: Boolean) {
        uiState.checkState<MeetingDetailUiState.Success> {
            if (isPlanSelected == isBefore) return@checkState
            setUiState(copy(isPlanSelected = isBefore))
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
                        planTime = ZonedDateTime.now().toString()
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
        val meeting: Meeting = Meeting(),
        val plans: List<Plan> = emptyList(),
        val reviews: List<Review> = emptyList(),
        val isPlanSelected: Boolean = true,
    ) : MeetingDetailUiState

    data object Error : MeetingDetailUiState
}

sealed interface MeetingDetailUiAction : UiAction {
    data object OnClickBack : MeetingDetailUiAction
    data object OnClickRefresh : MeetingDetailUiAction
    data object OnClickPlanWrite : MeetingDetailUiAction
    data object OnClickMeetingSetting : MeetingDetailUiAction
    data class OnClickPlanTab(val isBefore: Boolean) : MeetingDetailUiAction
    data class OnClickPlanApply(val planId: String, val isApply: Boolean) : MeetingDetailUiAction
    data class OnClickPlanDetail(val postId: String, val isPlan: Boolean) : MeetingDetailUiAction
}

sealed interface MeetingDetailUiEvent : UiEvent {
    data object NavigateToBack : MeetingDetailUiEvent
    data class NavigateToPlanWrite(val plan: Plan) : MeetingDetailUiEvent
    data class NavigateToMeetingSetting(val meeting: Meeting) : MeetingDetailUiEvent
    data class NavigateToPlanDetail(val postId: String, val isPlan: Boolean) : MeetingDetailUiEvent
    data class ShowToastMessage(val message: ToastMessage) : MeetingDetailUiEvent
}