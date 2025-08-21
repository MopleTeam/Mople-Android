package com.moim.core.model

import androidx.compose.runtime.Stable

@Stable
data class User(
    val userId: String,
    val nickname: String = "",
    val profileUrl: String = "",
    val badgeCount: Int = 0,
    val userRole: String = ""
)
