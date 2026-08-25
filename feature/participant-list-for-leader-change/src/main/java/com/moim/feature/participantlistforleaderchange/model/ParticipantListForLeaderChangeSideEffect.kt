package com.moim.feature.participantlistforleaderchange.model

import com.moim.core.common.model.User

sealed interface ParticipantListForLeaderChangeSideEffect {
    data object NavigateToBack : ParticipantListForLeaderChangeSideEffect

    data object NavigateToExit : ParticipantListForLeaderChangeSideEffect

    data class NavigateToImageViewer(
        val user: User,
    ) : ParticipantListForLeaderChangeSideEffect

    data object ShowCompletedMessage : ParticipantListForLeaderChangeSideEffect

    data object ShowErrorMessage : ParticipantListForLeaderChangeSideEffect
}
