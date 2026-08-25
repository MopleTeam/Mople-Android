package com.moim.feature.meeting.model

import com.moim.core.ui.mvi.Intent

sealed interface MeetingIntent : Intent {
    data class MeetingClick(
        val meetingId: String,
    ) : MeetingIntent

    data object MeetingWriteClick : MeetingIntent

    data object RefreshClick : MeetingIntent

    data object NextPageLoad : MeetingIntent
}
