package com.moim.core.common.model

import androidx.compose.runtime.Stable

@Stable
data class Place(
    val title: String = "",
    val address: String = "",
    val distance: String = "",
    val roadAddress: String = "",
    val xPoint: String = "",
    val yPoint: String = "",
)
