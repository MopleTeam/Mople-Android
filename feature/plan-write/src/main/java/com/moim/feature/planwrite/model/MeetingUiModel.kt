package com.moim.feature.planwrite.model

import com.moim.core.model.Meeting

data class MeetingUiModel(
    val meeting: Meeting,
    val isSelected: Boolean = false,
)