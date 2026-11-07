package com.moim.feature.meetingnoticewrite.model

import com.moim.core.ui.view.ToastMessage

sealed interface MeetingNoticeWriteSideEffect {
    data object NavigateToBack : MeetingNoticeWriteSideEffect

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : MeetingNoticeWriteSideEffect
}
