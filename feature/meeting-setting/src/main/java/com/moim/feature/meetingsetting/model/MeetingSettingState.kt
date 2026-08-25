package com.moim.feature.meetingsetting.model

import androidx.compose.runtime.Immutable
import com.moim.core.common.model.Meeting

@Immutable
data class MeetingSettingState(
    val meeting: Meeting = Meeting(),
    val isHostUser: Boolean = false,
    val isShowMeetingDeleteDialog: Boolean = false,
    val isShowMeetingExitDialog: Boolean = false,
)
