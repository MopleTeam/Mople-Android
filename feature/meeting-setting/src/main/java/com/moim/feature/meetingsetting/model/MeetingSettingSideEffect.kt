package com.moim.feature.meetingsetting.model

import com.moim.core.common.model.Meeting
import com.moim.core.common.model.ViewIdType
import com.moim.core.ui.view.ToastMessage

sealed interface MeetingSettingSideEffect {
    data object NavigateToBack : MeetingSettingSideEffect

    data object NavigateToBackForDelete : MeetingSettingSideEffect

    data class NavigateToMeetingWrite(
        val meeting: Meeting,
    ) : MeetingSettingSideEffect

    data class NavigateToMeetingParticipants(
        val viewIdType: ViewIdType,
    ) : MeetingSettingSideEffect

    data class NavigateToParticipantsForLeaderChange(
        val viewIdType: ViewIdType.MeetId,
    ) : MeetingSettingSideEffect

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : MeetingSettingSideEffect
}
