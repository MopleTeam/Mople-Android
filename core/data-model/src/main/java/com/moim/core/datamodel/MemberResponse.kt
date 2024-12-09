package com.moim.core.datamodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberResponse(
    @SerialName("nickname")
    val userNickname: String,
    @SerialName("profileImg")
    val imageUrl: String? = null,
)