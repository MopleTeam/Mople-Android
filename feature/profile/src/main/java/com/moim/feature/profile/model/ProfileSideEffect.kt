package com.moim.feature.profile.model

import com.moim.core.ui.view.ToastMessage

sealed interface ProfileSideEffect {
    data object NavigateToProfileUpdate : ProfileSideEffect

    data object NavigateToAlarmSetting : ProfileSideEffect

    data object NavigateToThemeSetting : ProfileSideEffect

    data object NavigateToPrivacyPolicy : ProfileSideEffect

    data object NavigateToUserWithdrawalForLeaderChange : ProfileSideEffect

    data object NavigateToIntro : ProfileSideEffect

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : ProfileSideEffect
}
