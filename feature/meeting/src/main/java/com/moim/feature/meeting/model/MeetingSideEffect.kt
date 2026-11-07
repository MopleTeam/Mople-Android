package com.moim.feature.meeting.model

sealed interface MeetingSideEffect {
    data object NavigateToMeetingWrite : MeetingSideEffect

    data class NavigateToMeetingDetail(
        val meetingId: String,
    ) : MeetingSideEffect
}
