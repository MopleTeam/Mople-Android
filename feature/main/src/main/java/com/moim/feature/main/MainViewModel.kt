package com.moim.feature.main

import androidx.lifecycle.viewModelScope
import com.moim.core.common.delegate.MeetingViewModelDelegate
import com.moim.core.common.delegate.PlanItemViewModelDelegate
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiEvent
import com.moim.core.data.datasource.meeting.MeetingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val meetingRepository: MeetingRepository,
    meetingViewModelDelegate: MeetingViewModelDelegate,
    planItemViewModelDelegate: PlanItemViewModelDelegate,
) : BaseViewModel(),
    PlanItemViewModelDelegate by planItemViewModelDelegate,
    MeetingViewModelDelegate by meetingViewModelDelegate {

    fun setPlanId(planId: String) {
        invalidatePlanItem(ZonedDateTime.now())
        setUiEvent(MainUiEvent.NavigateToPlanDetail(ViewIdType.PlanId(planId)))
    }

    fun setReviewId(reviewId: String) {
        invalidatePlanItem(ZonedDateTime.now())
        setUiEvent(MainUiEvent.NavigateToPlanDetail(ViewIdType.ReviewId(reviewId)))
    }

    fun setMeetingId(meetingId: String) {
        invalidateMeeting(ZonedDateTime.now())
        setUiEvent(MainUiEvent.NavigateToMeetingDetail(meetingId))
    }

    fun setJoinMeeting(meetCode: String) {
        viewModelScope.launch {
            runCatching {
                val meeting = meetingRepository.joinMeeting(meetCode).first()
                invalidateMeeting(ZonedDateTime.now())
                setUiEvent(MainUiEvent.NavigateToMeetingDetail(meeting.id))
            }
        }
    }
}

sealed interface MainUiEvent : UiEvent {
    data class NavigateToPlanDetail(val viewIdType: ViewIdType) : MainUiEvent
    data class NavigateToMeetingDetail(val meetingId: String) : MainUiEvent
}