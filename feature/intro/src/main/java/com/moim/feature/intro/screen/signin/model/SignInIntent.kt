package com.moim.feature.intro.screen.signin.model

import com.moim.core.ui.mvi.Intent

sealed interface SignInIntent : Intent {
    data object KakaoLoginClick : SignInIntent
}
