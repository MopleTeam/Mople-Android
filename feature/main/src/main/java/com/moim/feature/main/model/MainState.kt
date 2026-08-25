package com.moim.feature.main.model

import androidx.compose.runtime.Immutable
import com.moim.core.common.model.Theme

@Immutable
data class MainState(
    val theme: Theme = Theme.SYSTEM,
)
