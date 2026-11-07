package com.moim.feature.themesetting.model

sealed interface ThemeSettingSideEffect {
    data object NavigateToBack : ThemeSettingSideEffect
}
