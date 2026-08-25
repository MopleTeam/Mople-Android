package com.moim.feature.alarm.model

import com.moim.core.common.model.ViewIdType

sealed interface AlarmSideEffect {
    data object NavigateToBack : AlarmSideEffect

    data class NavigateToMeetingDetail(
        val meetingId: String,
    ) : AlarmSideEffect

    data class NavigateToPlanDetail(
        val viewIdType: ViewIdType,
    ) : AlarmSideEffect
}
