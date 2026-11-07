package com.moim.feature.profileupdate.model

import com.moim.core.ui.view.ToastMessage

sealed interface ProfileUpdateSideEffect {
    data object NavigateToBack : ProfileUpdateSideEffect

    data object NavigateToPhotoPicker : ProfileUpdateSideEffect

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : ProfileUpdateSideEffect
}
