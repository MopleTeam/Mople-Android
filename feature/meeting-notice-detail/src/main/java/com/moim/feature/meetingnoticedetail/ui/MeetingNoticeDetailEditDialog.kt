package com.moim.feature.meetingnoticedetail.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.moim.core.common.model.NoticeComment
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimAlertDialog
import com.moim.feature.meetingnoticedetail.OnMeetingNoticeDetailIntent
import com.moim.feature.meetingnoticedetail.model.MeetingNoticeDetailIntent

@Composable
fun MeetingNoticeDetailEditDialog(onIntent: OnMeetingNoticeDetailIntent) {
    val dismissIntent = MeetingNoticeDetailIntent.NoticeEditDialogShow(false)

    MoimAlertDialog(
        title = stringResource(R.string.meeting_notice_detail_title),
        description = stringResource(R.string.meeting_notice_detail_update_title),
        negativeText = stringResource(R.string.meeting_notice_detail_delete),
        positiveText = stringResource(R.string.meeting_notice_detail_update),
        onDismiss = { onIntent(dismissIntent) },
        onClickNegative = {
            onIntent(dismissIntent)
            onIntent(MeetingNoticeDetailIntent.NoticeDeleteClick)
        },
        onClickPositive = {
            onIntent(dismissIntent)
            onIntent(MeetingNoticeDetailIntent.NoticeUpdateClick)
        },
    )
}

@Composable
fun MeetingNoticeDetailCommentEditDialog(
    comment: NoticeComment,
    onIntent: OnMeetingNoticeDetailIntent,
) {
    val dismissIntent = MeetingNoticeDetailIntent.CommentEditDialogShow(false)

    MoimAlertDialog(
        title = stringResource(R.string.plan_detail_edit),
        description = stringResource(R.string.plan_detail_update_title, stringResource(R.string.plan_detail_comment)),
        negativeText = stringResource(R.string.plan_detail_delete),
        positiveText = stringResource(R.string.plan_detail_update),
        onDismiss = { onIntent(dismissIntent) },
        onClickNegative = {
            onIntent(dismissIntent)
            onIntent(MeetingNoticeDetailIntent.CommentDeleteClick(comment))
        },
        onClickPositive = {
            onIntent(dismissIntent)
            onIntent(MeetingNoticeDetailIntent.CommentUpdateClick(comment))
        },
    )
}

@Composable
fun MeetingNoticeDetailCommentReportDialog(
    comment: NoticeComment,
    onIntent: OnMeetingNoticeDetailIntent,
) {
    val dismissIntent = MeetingNoticeDetailIntent.CommentReportDialogShow(false)

    MoimAlertDialog(
        title = stringResource(R.string.plan_detail_report_title, stringResource(R.string.plan_detail_comment)),
        description = stringResource(R.string.plan_detail_report_description),
        negativeText = stringResource(R.string.common_negative),
        positiveText = stringResource(R.string.plan_detail_report),
        onDismiss = { onIntent(dismissIntent) },
        onClickNegative = { onIntent(dismissIntent) },
        onClickPositive = {
            onIntent(dismissIntent)
            onIntent(MeetingNoticeDetailIntent.CommentReportClick(comment))
        },
    )
}
