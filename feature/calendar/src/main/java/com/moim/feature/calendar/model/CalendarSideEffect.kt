package com.moim.feature.calendar.model

import com.moim.core.common.model.ViewIdType
import com.moim.core.ui.view.ToastMessage

sealed interface CalendarSideEffect {
    data class NavigateToPlanDetail(
        val viewIdType: ViewIdType,
    ) : CalendarSideEffect

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : CalendarSideEffect
}
