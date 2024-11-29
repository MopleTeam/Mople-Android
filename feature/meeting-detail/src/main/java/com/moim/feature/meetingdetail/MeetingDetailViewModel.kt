package com.moim.feature.meetingdetail

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.common.view.checkState
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.data.model.PlanResponse
import com.moim.core.data.model.ReviewResponse
import com.moim.core.model.Meeting
import com.moim.core.model.Plan
import com.moim.core.model.Review
import com.moim.core.model.asItem
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
) : BaseViewModel() {

    private val meetingId
        get() = savedStateHandle.get<String>(KEY_MEETING_ID) ?: ""

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
            meetingDetailResult.collect { result ->
                when (result) {
                    is Result.Loading -> setUiState(MeetingDetailUiState.Loading)
                    is Result.Success -> {
                        val (meeting, plans, reviews) = result.data

                        setUiState(
                            MeetingDetailUiState.Success(
                                meeting = meeting.asItem(),
                                plans = plans.map(PlanResponse::asItem),
                                reviews = reviews.map(ReviewResponse::asItem)
                            )
                        )
                    }

                    is Result.Error -> setUiState(MeetingDetailUiState.Error)
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
            is MeetingDetailUiAction.OnClickPlanDetail -> setUiEvent(MeetingDetailUiEvent.NavigateToPlanDetail(uiAction.planId))
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
                        meetingImage = meeting.imageUrl,
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
    data class OnClickPlanDetail(val planId: String) : MeetingDetailUiAction
}

sealed interface MeetingDetailUiEvent : UiEvent {
    data object NavigateToBack : MeetingDetailUiEvent
    data class NavigateToPlanWrite(val plan: Plan) : MeetingDetailUiEvent
    data class NavigateToMeetingSetting(val meeting: Meeting) : MeetingDetailUiEvent
    data class NavigateToPlanDetail(val planId: String) : MeetingDetailUiEvent
    data class ShowToastMessage(@StringRes val messageRes: Int) : MeetingDetailUiEvent
}