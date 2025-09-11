package com.moim.core.common.model

import androidx.compose.runtime.Stable
import java.time.ZonedDateTime

@Stable
data class Holiday(
    val title: String,
    val date: ZonedDateTime
)
