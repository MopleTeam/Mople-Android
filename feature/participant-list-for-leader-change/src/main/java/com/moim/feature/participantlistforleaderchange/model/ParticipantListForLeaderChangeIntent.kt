package com.moim.feature.participantlistforleaderchange.model

import com.moim.core.common.model.User
import com.moim.core.ui.mvi.Intent

sealed interface ParticipantListForLeaderChangeIntent : Intent {
    data object BackClick : ParticipantListForLeaderChangeIntent

    data object RefreshClick : ParticipantListForLeaderChangeIntent

    data object NextPageLoad : ParticipantListForLeaderChangeIntent

    data class UserClick(
        val user: User,
    ) : ParticipantListForLeaderChangeIntent

    data class UserProfileClick(
        val user: User,
    ) : ParticipantListForLeaderChangeIntent

    data class LeaderChangeClick(
        val userId: String,
    ) : ParticipantListForLeaderChangeIntent

    data class ChangeLeaderDialogShow(
        val isShow: Boolean,
    ) : ParticipantListForLeaderChangeIntent
}
