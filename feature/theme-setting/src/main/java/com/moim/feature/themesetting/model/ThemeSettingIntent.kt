package com.moim.feature.themesetting.model

import com.moim.core.common.model.Theme
import com.moim.core.ui.mvi.Intent

sealed interface ThemeSettingIntent : Intent {
    data object BackClick : ThemeSettingIntent

    data class ThemeClick(
        val theme: Theme,
    ) : ThemeSettingIntent
}
