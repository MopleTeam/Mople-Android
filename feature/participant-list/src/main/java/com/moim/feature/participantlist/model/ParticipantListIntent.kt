package com.moim.feature.participantlist.model

import com.moim.core.ui.mvi.Intent

sealed interface ParticipantListIntent : Intent {
    data object BackClick : ParticipantListIntent

    data object RefreshClick : ParticipantListIntent

    data object MeetingInviteClick : ParticipantListIntent

    data object NextPageLoad : ParticipantListIntent

    data class UserImageClick(
        val userImage: String,
        val userName: String,
    ) : ParticipantListIntent
}
