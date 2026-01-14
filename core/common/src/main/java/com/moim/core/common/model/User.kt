package com.moim.core.common.model

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class User(
    val userId: String,
    val nickname: String = "",
    val profileUrl: String = "",
    val badgeCount: Int = 0,
    val userRole: String = "",
)
