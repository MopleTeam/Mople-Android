package com.moim.feature.meetingwrite.model

import com.moim.core.ui.view.ToastMessage

sealed interface MeetingWriteSideEffect {
    data object NavigateToBack : MeetingWriteSideEffect

    data object NavigateToPhotoPicker : MeetingWriteSideEffect

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : MeetingWriteSideEffect
}
