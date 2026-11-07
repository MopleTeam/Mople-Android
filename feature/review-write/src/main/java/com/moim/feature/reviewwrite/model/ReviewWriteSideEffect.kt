package com.moim.feature.reviewwrite.model

import com.moim.core.common.model.ViewIdType
import com.moim.core.ui.view.ToastMessage

sealed interface ReviewWriteSideEffect {
    data object NavigateToBack : ReviewWriteSideEffect

    data object NavigateToPhotoPicker : ReviewWriteSideEffect

    data class NavigateToParticipants(
        val viewIdType: ViewIdType,
    ) : ReviewWriteSideEffect

    data class ShowToastMessage(
        val toastMessage: ToastMessage,
    ) : ReviewWriteSideEffect
}
