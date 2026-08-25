package com.moim.feature.meetingwrite.model

import androidx.compose.runtime.Immutable

@Immutable
data class MeetingWriteState(
    val meetingId: String? = null,
    val meetingUrl: String? = null,
    val meetingName: String = "",
    val enableMeetingWrite: Boolean = false,
    val isShowPhotoEditDialog: Boolean = false,
)
