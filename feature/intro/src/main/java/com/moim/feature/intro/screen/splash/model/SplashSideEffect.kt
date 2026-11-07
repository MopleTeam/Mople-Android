package com.moim.feature.intro.screen.splash.model

sealed interface SplashSideEffect {
    data object NavigateToSignIn : SplashSideEffect

    data object NavigateToMain : SplashSideEffect

    data object NavigateToExit : SplashSideEffect

    data object NavigateToPlayStore : SplashSideEffect
}
