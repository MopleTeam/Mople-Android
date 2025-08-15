package com.moim.core.datamodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("userId")
    val userId: String = "",
    @SerialName("nickname")
    val nickname: String = "",
    @SerialName("image")
    val profileUrl: String = "",
    @SerialName("badgeCount")
    val badgeCount: Int = 0,
    @SerialName("userRole")
    val userRole: String = "",
)