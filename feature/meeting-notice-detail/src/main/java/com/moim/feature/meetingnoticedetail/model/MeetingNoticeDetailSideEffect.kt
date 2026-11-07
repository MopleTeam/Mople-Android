package com.moim.feature.meetingnoticedetail.model

import com.moim.core.ui.view.ToastMessage

sealed interface MeetingNoticeDetailSideEffect {
    data object NavigateToBack : MeetingNoticeDetailSideEffect

    data class NavigateToMeetingNoticeWrite(
        val meetId: String,
        val noticeId: String,
    ) : MeetingNoticeDetailSideEffect

    data class NavigateToWebBrowser(
        val webLink: String,
    ) : MeetingNoticeDetailSideEffect

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : MeetingNoticeDetailSideEffect
}
