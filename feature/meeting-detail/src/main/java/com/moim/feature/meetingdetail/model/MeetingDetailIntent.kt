package com.moim.feature.meetingdetail.model

import com.moim.core.common.model.ViewIdType
import com.moim.core.common.model.item.PlanItem
import com.moim.core.ui.mvi.Intent

sealed interface MeetingDetailIntent : Intent {
    data object BackClick : MeetingDetailIntent

    data object RefreshClick : MeetingDetailIntent

    data object PlanWriteClick : MeetingDetailIntent

    data object MeetingSettingClick : MeetingDetailIntent

    data object MeetingNoticeClick : MeetingDetailIntent

    data class MeetingNoticeDetailClick(
        val noticeId: String,
    ) : MeetingDetailIntent

    data object MeetingInviteClick : MeetingDetailIntent

    data object NextPageLoad : MeetingDetailIntent

    data class PlanTabClick(
        val isBefore: Boolean,
    ) : MeetingDetailIntent

    data class PlanApplyClick(
        val planItem: PlanItem,
        val isApply: Boolean,
    ) : MeetingDetailIntent

    data class PlanDetailClick(
        val viewIdType: ViewIdType,
    ) : MeetingDetailIntent

    data class MeetingImageClick(
        val imageUrl: String,
        val meetingName: String,
    ) : MeetingDetailIntent

    data class PlanApplyCancelDialogShow(
        val isShow: Boolean,
        val cancelPlanItem: PlanItem?,
    ) : MeetingDetailIntent
}
