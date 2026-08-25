package com.moim.feature.main.model

import com.moim.core.common.model.ViewIdType

sealed interface MainSideEffect {
    data class NavigateToPlanDetail(
        val viewIdType: ViewIdType,
    ) : MainSideEffect

    data class NavigateToMeetingDetail(
        val meetingId: String,
    ) : MainSideEffect
}
