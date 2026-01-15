package com.moim.core.remote.model

import com.moim.core.common.model.User
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
    @SerialName("role")
    val userRole: String = "",
)

fun UserResponse.asItem(): User =
    User(
        userId = userId,
        nickname = nickname,
        profileUrl = profileUrl,
        badgeCount = badgeCount,
        userRole = userRole,
    )
