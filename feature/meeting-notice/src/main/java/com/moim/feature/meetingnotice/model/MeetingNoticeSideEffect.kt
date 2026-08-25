package com.moim.feature.meetingnotice.model

import com.moim.core.ui.view.ToastMessage

sealed interface MeetingNoticeSideEffect {
    data object NavigateToBack : MeetingNoticeSideEffect

    data class NavigateToMeetingNoticeWrite(
        val meetId: String,
    ) : MeetingNoticeSideEffect

    data class NavigateToMeetingNoticeDetail(
        val meetId: String,
        val noticeId: String,
    ) : MeetingNoticeSideEffect

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : MeetingNoticeSideEffect
}
