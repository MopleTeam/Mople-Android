package com.moim.core.data.mapper

import com.moim.core.datamodel.UserResponse
import com.moim.core.model.User


fun UserResponse.asItem(): User {
    return User(
        userId = userId,
        nickname = nickname,
        profileUrl = profileUrl,
        badgeCount = badgeCount,
        userRole = userRole
    )
}