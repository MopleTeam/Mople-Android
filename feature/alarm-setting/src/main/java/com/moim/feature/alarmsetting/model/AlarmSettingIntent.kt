package com.moim.feature.alarmsetting.model

import com.moim.core.ui.mvi.Intent

sealed interface AlarmSettingIntent : Intent {
    data object BackClick : AlarmSettingIntent

    data object RefreshClick : AlarmSettingIntent

    data object PermissionRequestClick : AlarmSettingIntent

    data class MeetingNotifyChange(
        val isCheck: Boolean,
    ) : AlarmSettingIntent

    data class PlanNotifyChange(
        val isCheck: Boolean,
    ) : AlarmSettingIntent

    data class CommentNotifyChange(
        val isCheck: Boolean,
    ) : AlarmSettingIntent

    data class MentionNotifyChange(
        val isCheck: Boolean,
    ) : AlarmSettingIntent
}
