package com.moim.feature.meetingdetail.model

import com.moim.core.common.model.ViewIdType
import com.moim.core.common.model.item.PlanItem
import com.moim.core.ui.view.UiAction

sealed interface MeetingDetailUiAction : UiAction {
    data object OnClickBack : MeetingDetailUiAction

    data object OnClickRefresh : MeetingDetailUiAction

    data object OnClickPlanWrite : MeetingDetailUiAction

    data object OnClickMeetingSetting : MeetingDetailUiAction

    data object OnClickMeetingNotice : MeetingDetailUiAction

    data class OnClickMeetingNoticeDetail(
        val noticeId: String,
    ) : MeetingDetailUiAction

    data object OnClickMeetingInvite : MeetingDetailUiAction

    data object OnLoadNextPage : MeetingDetailUiAction

    data class OnClickPlanTab(
        val isBefore: Boolean,
    ) : MeetingDetailUiAction

    data class OnClickPlanApply(
        val planItem: PlanItem,
        val isApply: Boolean,
    ) : MeetingDetailUiAction

    data class OnClickPlanDetail(
        val viewIdType: ViewIdType,
    ) : MeetingDetailUiAction

    data class OnClickMeetingImage(
        val imageUrl: String,
        val meetingName: String,
    ) : MeetingDetailUiAction

    data class OnShowPlanApplyCancelDialog(
        val isShow: Boolean,
        val cancelPlanItem: PlanItem?,
    ) : MeetingDetailUiAction
}
