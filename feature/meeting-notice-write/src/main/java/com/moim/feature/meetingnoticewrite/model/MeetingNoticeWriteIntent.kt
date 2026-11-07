package com.moim.feature.meetingnoticewrite.model

import com.moim.core.ui.mvi.Intent

sealed interface MeetingNoticeWriteIntent : Intent {
    data object BackClick : MeetingNoticeWriteIntent

    data object RefreshClick : MeetingNoticeWriteIntent

    data class EnableChange(
        val isEnable: Boolean,
    ) : MeetingNoticeWriteIntent

    data class ConfirmClick(
        val meetId: String,
        val noticeId: String?,
    ) : MeetingNoticeWriteIntent
}
