package com.moim.feature.intro.screen.splash.model

import androidx.compose.runtime.Immutable

@Immutable
data class SplashState(
    val isShowErrorDialog: Boolean = false,
    val isShowForceUpdateDialog: Boolean = false,
)
