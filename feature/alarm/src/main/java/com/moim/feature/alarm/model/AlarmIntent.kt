package com.moim.feature.alarm.model

import com.moim.core.ui.mvi.Intent

sealed interface AlarmIntent : Intent {
    data object BackClick : AlarmIntent

    data object RefreshClick : AlarmIntent

    data object NextPageLoad : AlarmIntent

    data object NotificationCountUpdate : AlarmIntent

    data class AlarmClick(
        val item: AlarmUiModel,
    ) : AlarmIntent
}
