package com.moim.core.model

import androidx.compose.runtime.Stable

@Stable
data class Member(
    val userNickname: String = "",
    val imageUrl: String? = null,
)
