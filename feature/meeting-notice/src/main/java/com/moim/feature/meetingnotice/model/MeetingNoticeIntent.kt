package com.moim.feature.meetingnotice.model

import com.moim.core.ui.mvi.Intent

sealed interface MeetingNoticeIntent : Intent {
    data object BackClick : MeetingNoticeIntent

    data object WriteClick : MeetingNoticeIntent

    data object RefreshClick : MeetingNoticeIntent

    data object NextPageLoad : MeetingNoticeIntent

    data class NoticeClick(
        val notice: NoticeUiModel,
    ) : MeetingNoticeIntent

    data class PinClick(
        val notice: NoticeUiModel,
    ) : MeetingNoticeIntent

    data class TabSelect(
        val tabIndex: Int,
    ) : MeetingNoticeIntent
}
