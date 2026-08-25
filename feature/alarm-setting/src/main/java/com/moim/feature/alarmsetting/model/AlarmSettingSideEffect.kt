package com.moim.feature.alarmsetting.model

import com.moim.core.ui.view.ToastMessage

sealed interface AlarmSettingSideEffect {
    data object NavigateToBack : AlarmSettingSideEffect

    data object NavigateToSystemSetting : AlarmSettingSideEffect

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : AlarmSettingSideEffect
}
