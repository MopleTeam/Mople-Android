package com.moim.feature.intro.screen.signup.model

import com.moim.core.ui.mvi.Intent

sealed interface SignUpIntent : Intent {
    data object SignUpClick : SignUpIntent

    data object DuplicatedCheckClick : SignUpIntent

    data object PhotoPickerClick : SignUpIntent

    data class ProfileUrlChange(
        val profileUrl: String?,
    ) : SignUpIntent

    data class NicknameChange(
        val nickname: String,
    ) : SignUpIntent

    data class ProfileEditDialogShow(
        val isShow: Boolean,
    ) : SignUpIntent
}
