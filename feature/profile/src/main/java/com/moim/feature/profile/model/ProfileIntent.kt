package com.moim.feature.profile.model

import com.moim.core.ui.mvi.Intent

sealed interface ProfileIntent : Intent {
    data object ProfileClick : ProfileIntent

    data object AlarmSettingClick : ProfileIntent

    data object ThemeSettingClick : ProfileIntent

    data object PrivacyPolicyClick : ProfileIntent

    data object LogoutClick : ProfileIntent

    data object UserWithdrawalClick : ProfileIntent

    data object UserDeleteClick : ProfileIntent

    data object RefreshClick : ProfileIntent

    data class UserLogoutDialogShow(
        val isShow: Boolean,
    ) : ProfileIntent

    data class UserDeleteDialogShow(
        val isShow: Boolean,
    ) : ProfileIntent
}
