package com.moim.feature.home.model

import com.moim.core.common.model.ViewIdType
import com.moim.core.ui.view.ToastMessage

sealed interface HomeSideEffect {
    data object NavigateToAlarm : HomeSideEffect

    data object NavigateToMeetingWrite : HomeSideEffect

    data object NavigateToPlanWrite : HomeSideEffect

    data object NavigateToCalendar : HomeSideEffect

    data class NavigateToPlanDetail(
        val viewIdType: ViewIdType,
    ) : HomeSideEffect

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : HomeSideEffect
}
