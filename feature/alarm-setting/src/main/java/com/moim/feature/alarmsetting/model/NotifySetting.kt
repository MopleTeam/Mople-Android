package com.moim.feature.alarmsetting.model

import androidx.compose.runtime.Immutable

@Immutable
data class NotifySetting(
    val isSubscribeForMeetingNotify: Boolean = false,
    val isSubscribeForPlanNotify: Boolean = false,
    val isSubscribeForCommentNotify: Boolean = false,
    val isSubscribeForMentionNotify: Boolean = false,
)
