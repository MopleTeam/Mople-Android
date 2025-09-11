package com.moim.core.common.model

import androidx.compose.runtime.Stable

@Stable
data class Member(
    val userNickname: String = "",
    val imageUrl: String? = null,
)
