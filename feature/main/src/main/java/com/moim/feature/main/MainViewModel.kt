package com.moim.feature.main

import androidx.lifecycle.viewModelScope
import com.moim.core.common.model.ViewIdType
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.MeetingAction
import com.moim.core.ui.eventbus.PlanAction
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val meetingRepository: MeetingRepository,
    private val meetingEventBus: EventBus<MeetingAction>,
    private val planEventBus: EventBus<PlanAction>,
) : BaseViewModel() {
    val theme = userRepository.getTheme()

    fun setPlanId(planId: String) {
        planEventBus.send(PlanAction.PlanInvalidate())
        setUiEvent(MainUiEvent.NavigateToPlanDetail(ViewIdType.PlanId(planId)))
    }

    fun setReviewId(reviewId: String) {
        planEventBus.send(PlanAction.PlanInvalidate())
        setUiEvent(MainUiEvent.NavigateToPlanDetail(ViewIdType.ReviewId(reviewId)))
    }

    fun setMeetingId(meetingId: String) {
        meetingEventBus.send(MeetingAction.MeetingInvalidate())
        setUiEvent(MainUiEvent.NavigateToMeetingDetail(meetingId))
    }

    fun setJoinMeeting(meetCode: String) {
        viewModelScope.launch {
            runCatching {
                val meeting = meetingRepository.joinMeeting(meetCode).first()
                meetingEventBus.send(MeetingAction.MeetingInvalidate())
                setUiEvent(MainUiEvent.NavigateToMeetingDetail(meeting.id))
            }
        }
    }
}

sealed interface MainUiEvent : UiEvent {
    data class NavigateToPlanDetail(
        val viewIdType: ViewIdType,
    ) : MainUiEvent

    data class NavigateToMeetingDetail(
        val meetingId: String,
    ) : MainUiEvent
}
