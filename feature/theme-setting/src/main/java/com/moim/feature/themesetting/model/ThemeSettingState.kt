package com.moim.feature.themesetting.model

import androidx.compose.runtime.Immutable
import com.moim.core.common.model.Theme

@Immutable
data class ThemeSettingState(
    val theme: Theme = Theme.SYSTEM,
)
