package com.moim.feature.meetingwrite.model

import com.moim.core.ui.mvi.Intent

sealed interface MeetingWriteIntent : Intent {
    data object BackClick : MeetingWriteIntent

    data object MeetingWriteClick : MeetingWriteIntent

    data object PhotoPickerClick : MeetingWriteIntent

    data class MeetingPhotoUrlChange(
        val meetingPhotoUrl: String?,
    ) : MeetingWriteIntent

    data class MeetingNameChange(
        val name: String,
    ) : MeetingWriteIntent

    data class MeetingPhotoEditDialogShow(
        val isShow: Boolean,
    ) : MeetingWriteIntent
}
