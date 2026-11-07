package com.moim.feature.meetingsetting.model

import com.moim.core.common.model.Meeting
import com.moim.core.common.model.ViewIdType
import com.moim.core.ui.mvi.Intent

sealed interface MeetingSettingIntent : Intent {
    data object BackClick : MeetingSettingIntent

    data object MeetingExitClick : MeetingSettingIntent

    data class MeetingParticipantsClick(
        val viewIdType: ViewIdType,
    ) : MeetingSettingIntent

    data class MeetingLeaderChangeClick(
        val viewIdType: ViewIdType.MeetId,
    ) : MeetingSettingIntent

    data class MeetingEditClick(
        val meeting: Meeting,
    ) : MeetingSettingIntent

    data class MeetingExitDialogShow(
        val isShow: Boolean,
    ) : MeetingSettingIntent

    data class MeetingDeleteDialogShow(
        val isShow: Boolean,
    ) : MeetingSettingIntent
}
