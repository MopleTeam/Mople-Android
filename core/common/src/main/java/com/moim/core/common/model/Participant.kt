package com.moim.core.common.model

import kotlinx.serialization.Serializable

@Serializable
data class Participant(
    val isCreator: Boolean,
    val memberId: String,
    val nickname: String,
    val imageUrl: String,
)
