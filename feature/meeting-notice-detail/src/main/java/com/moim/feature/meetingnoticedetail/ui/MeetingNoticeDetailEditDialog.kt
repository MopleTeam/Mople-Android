package com.moim.feature.meetingnoticedetail.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimAlertDialog
import com.moim.feature.meetingnoticedetail.MeetingNoticeDetailUiAction
import com.moim.feature.meetingnoticedetail.OnMeetingNoticeDetailUiAction

@Composable
fun MeetingNoticeDetailEditDialog(onUiAction: OnMeetingNoticeDetailUiAction) {
    val dismissAction = MeetingNoticeDetailUiAction.OnShowNoticeEditDialog(false)

    MoimAlertDialog(
        title = stringResource(R.string.meeting_notice_detail_title),
        description = stringResource(R.string.meeting_notice_detail_update_title),
        negativeText = stringResource(R.string.meeting_notice_detail_delete),
        positiveText = stringResource(R.string.meeting_notice_detail_update),
        onDismiss = { onUiAction(dismissAction) },
        onClickNegative = {
            onUiAction(dismissAction)
            onUiAction(MeetingNoticeDetailUiAction.OnClickNoticeDelete)
        },
        onClickPositive = {
            onUiAction(dismissAction)
            onUiAction(MeetingNoticeDetailUiAction.OnClickNoticeUpdate)
        },
    )
}
