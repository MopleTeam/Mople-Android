package com.moim.feature.intro.screen.splash.model

import com.moim.core.ui.mvi.Intent

sealed interface SplashIntent : Intent {
    data object ExitClick : SplashIntent

    data object ForceUpdateClick : SplashIntent
}
