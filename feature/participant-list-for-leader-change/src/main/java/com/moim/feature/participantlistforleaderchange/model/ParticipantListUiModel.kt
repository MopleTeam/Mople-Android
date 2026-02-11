package com.moim.feature.participantlistforleaderchange.model

import com.moim.core.common.model.User

data class ParticipantListUiModel(
    val user: User,
    val isSelected: Boolean,
)
