package com.moim.feature.home.model

import com.moim.core.ui.mvi.Intent

sealed interface HomeIntent : Intent {
    data object RefreshClick : HomeIntent

    data object AlarmClick : HomeIntent

    data object MeetingWriteClick : HomeIntent

    data object PlanWriteClick : HomeIntent

    data object PlanMoreClick : HomeIntent

    data class PlanClick(
        val planId: String,
        val isPlan: Boolean,
    ) : HomeIntent

    data object PermissionCheckUpdate : HomeIntent
}
