package com.moim.feature.meetingdetail.model

import com.moim.core.common.model.Meeting
import com.moim.core.common.model.Plan
import com.moim.core.common.model.ViewIdType
import com.moim.core.ui.view.ToastMessage

sealed interface MeetingDetailSideEffect {
    data object NavigateToBack : MeetingDetailSideEffect

    data class NavigateToPlanWrite(
        val plan: Plan,
    ) : MeetingDetailSideEffect

    data class NavigateToMeetingSetting(
        val meeting: Meeting,
    ) : MeetingDetailSideEffect

    data class NavigateToMeetingNotice(
        val meetId: String,
    ) : MeetingDetailSideEffect

    data class NavigateToMeetingNoticeDetail(
        val meetId: String,
        val noticeId: String,
    ) : MeetingDetailSideEffect

    data class NavigateToPlanDetail(
        val viewIdType: ViewIdType,
    ) : MeetingDetailSideEffect

    data class NavigateToImageViewer(
        val imageUrl: String,
        val meetingName: String,
    ) : MeetingDetailSideEffect

    data class NavigateToExternalShareUrl(
        val url: String,
    ) : MeetingDetailSideEffect

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : MeetingDetailSideEffect
}
