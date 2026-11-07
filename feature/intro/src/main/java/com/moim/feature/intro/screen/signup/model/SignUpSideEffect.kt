package com.moim.feature.intro.screen.signup.model

import com.moim.core.ui.view.ToastMessage

sealed interface SignUpSideEffect {
    data object NavigateToPhotoPicker : SignUpSideEffect

    data object NavigateToMain : SignUpSideEffect

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : SignUpSideEffect
}
