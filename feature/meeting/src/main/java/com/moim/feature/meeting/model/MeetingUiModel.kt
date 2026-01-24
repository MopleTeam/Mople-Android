package com.moim.feature.meeting.model

import com.moim.core.common.model.Meeting

data class MeetingUiModel(
    val meeting: Meeting,
    val isLeader: Boolean,
)
