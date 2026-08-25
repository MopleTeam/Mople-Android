package com.moim.feature.intro.screen.signin.model

import com.moim.core.ui.view.ToastMessage

sealed interface SignInSideEffect {
    data class NavigateToSignUp(
        val email: String,
        val token: String,
    ) : SignInSideEffect

    data object NavigateToMain : SignInSideEffect

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : SignInSideEffect
}
