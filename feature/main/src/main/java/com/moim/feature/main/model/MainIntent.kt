package com.moim.feature.main.model

import com.moim.core.ui.mvi.Intent

sealed interface MainIntent : Intent {
    data class PlanNotifyReceive(
        val planId: String,
    ) : MainIntent

    data class ReviewNotifyReceive(
        val reviewId: String,
    ) : MainIntent

    data class MeetingNotifyReceive(
        val meetingId: String,
    ) : MainIntent

    data class MeetingInviteReceive(
        val meetCode: String,
    ) : MainIntent
}
