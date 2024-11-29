package com.moim.core.model

import androidx.compose.runtime.Stable
import com.moim.core.data.model.UserResponse

@Stable
data class User(
    val userId: String,
    val nickname: String = "",
    val profileUrl: String = "",
    val badgeCount: Int = 0,
)

fun UserResponse.asItem(): User {
    return User(
        userId = userId,
        nickname = nickname,
        profileUrl = profileUrl,
        badgeCount = badgeCount,
    )
}