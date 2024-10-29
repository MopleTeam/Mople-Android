package com.moim.core.model

import androidx.compose.runtime.Stable
import com.moim.core.data.model.UserResponse

@Stable
data class User(
    val nickname: String = "",
    val profileUrl: String = "",
    val badgeCount: Int = 0,
)

fun UserResponse.asItem(): User {
    return User(
        nickname = nickname,
        profileUrl = profileUrl,
        badgeCount = badgeCount,
    )
}

fun User.asInternalModel(): UserResponse {
    return UserResponse(
        nickname = nickname,
        profileUrl = profileUrl,
        badgeCount = badgeCount,
    )
}