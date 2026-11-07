package com.moim.feature.participantlist.model

import com.moim.core.ui.view.ToastMessage

sealed interface ParticipantListSideEffect {
    data object NavigateToBack : ParticipantListSideEffect

    data class NavigateToImageViewer(
        val userImage: String,
        val userName: String,
    ) : ParticipantListSideEffect

    data class NavigateToExternalShareUrl(
        val url: String,
    ) : ParticipantListSideEffect

    data class ShowToastMessage(
        val toastMessage: ToastMessage,
    ) : ParticipantListSideEffect
}
