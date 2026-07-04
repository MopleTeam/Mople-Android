package com.moim.feature.meetingnoticedetail.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.moim.core.common.model.NoticeComment
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

@Composable
fun MeetingNoticeDetailCommentEditDialog(
    comment: NoticeComment,
    onUiAction: OnMeetingNoticeDetailUiAction,
) {
    val dismissAction = MeetingNoticeDetailUiAction.OnShowCommentEditDialog(false)

    MoimAlertDialog(
        title = stringResource(R.string.plan_detail_edit),
        description = stringResource(R.string.plan_detail_update_title, stringResource(R.string.plan_detail_comment)),
        negativeText = stringResource(R.string.plan_detail_delete),
        positiveText = stringResource(R.string.plan_detail_update),
        onDismiss = { onUiAction(dismissAction) },
        onClickNegative = {
            onUiAction(dismissAction)
            onUiAction(MeetingNoticeDetailUiAction.OnClickCommentDelete(comment))
        },
        onClickPositive = {
            onUiAction(dismissAction)
            onUiAction(MeetingNoticeDetailUiAction.OnClickCommentUpdate(comment))
        },
    )
}

@Composable
fun MeetingNoticeDetailCommentReportDialog(
    comment: NoticeComment,
    onUiAction: OnMeetingNoticeDetailUiAction,
) {
    val dismissAction = MeetingNoticeDetailUiAction.OnShowCommentReportDialog(false)

    MoimAlertDialog(
        title = stringResource(R.string.plan_detail_report_title, stringResource(R.string.plan_detail_comment)),
        description = stringResource(R.string.plan_detail_report_description),
        negativeText = stringResource(R.string.common_negative),
        positiveText = stringResource(R.string.plan_detail_report),
        onDismiss = { onUiAction(dismissAction) },
        onClickNegative = { onUiAction(dismissAction) },
        onClickPositive = {
            onUiAction(dismissAction)
            onUiAction(MeetingNoticeDetailUiAction.OnClickCommentReport(comment))
        },
    )
}
