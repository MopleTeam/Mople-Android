package com.moim.feature.meetingdetail.model

import com.moim.core.common.model.Meeting
import com.moim.core.common.model.Plan
import com.moim.core.common.model.ViewIdType
import com.moim.core.ui.view.ToastMessage
import com.moim.core.ui.view.UiEvent

sealed interface MeetingDetailUiEvent : UiEvent {
    data object NavigateToBack : MeetingDetailUiEvent

    data class NavigateToPlanWrite(
        val plan: Plan,
    ) : MeetingDetailUiEvent

    data class NavigateToMeetingSetting(
        val meeting: Meeting,
    ) : MeetingDetailUiEvent

    data class NavigateToMeetingNotice(
        val meetId: String,
    ) : MeetingDetailUiEvent

    data class NavigateToMeetingNoticeDetail(
        val meetId: String,
        val noticeId: String,
    ) : MeetingDetailUiEvent

    data class NavigateToPlanDetail(
        val viewIdType: ViewIdType,
    ) : MeetingDetailUiEvent

    data class NavigateToImageViewer(
        val imageUrl: String,
        val meetingName: String,
    ) : MeetingDetailUiEvent

    data class NavigateToExternalShareUrl(
        val url: String,
    ) : MeetingDetailUiEvent

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : MeetingDetailUiEvent
}
