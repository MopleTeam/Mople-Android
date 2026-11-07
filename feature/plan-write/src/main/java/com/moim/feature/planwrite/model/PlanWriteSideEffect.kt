package com.moim.feature.planwrite.model

import com.moim.core.ui.view.ToastMessage

sealed interface PlanWriteSideEffect {
    data object NavigateToBack : PlanWriteSideEffect

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : PlanWriteSideEffect
}
