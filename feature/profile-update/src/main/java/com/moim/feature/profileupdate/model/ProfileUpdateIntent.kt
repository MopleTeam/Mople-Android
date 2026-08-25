package com.moim.feature.profileupdate.model

import com.moim.core.ui.mvi.Intent

sealed interface ProfileUpdateIntent : Intent {
    data object BackClick : ProfileUpdateIntent

    data object RefreshClick : ProfileUpdateIntent

    data object ProfileUpdateClick : ProfileUpdateIntent

    data object DuplicatedCheckClick : ProfileUpdateIntent

    data object PhotoPickerClick : ProfileUpdateIntent

    data class ProfileUrlChange(
        val profileUrl: String?,
    ) : ProfileUpdateIntent

    data class NicknameChange(
        val nickname: String,
    ) : ProfileUpdateIntent

    data class ProfileEditDialogShow(
        val isShow: Boolean,
    ) : ProfileUpdateIntent
}
