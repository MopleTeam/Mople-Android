package com.moim.feature.commentdetail.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.moim.core.common.model.Comment
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimAlertDialog
import com.moim.feature.commentdetail.model.CommentDetailIntent

@Composable
fun CommentDetailEditDialog(
    comment: Comment,
    onIntent: (CommentDetailIntent) -> Unit,
) {
    val dismissAction = CommentDetailIntent.CommentEditDialogShow(false, null)

    MoimAlertDialog(
        title = stringResource(R.string.comment_detail_edit),
        description = stringResource(R.string.comment_detail_update_title),
        negativeText = stringResource(R.string.comment_detail_delete),
        positiveText = stringResource(R.string.comment_detail_update),
        onDismiss = { onIntent(dismissAction) },
        onClickNegative = {
            onIntent(dismissAction)
            onIntent(CommentDetailIntent.CommentDeleteClick(comment))
        },
        onClickPositive = {
            onIntent(dismissAction)
            onIntent(CommentDetailIntent.CommentUpdateClick(comment))
        },
    )
}

@Composable
fun CommentDetailReportDialog(
    comment: Comment,
    onIntent: (CommentDetailIntent) -> Unit,
) {
    val dismissAction = CommentDetailIntent.CommentReportDialogShow(false, null)

    MoimAlertDialog(
        title = stringResource(R.string.comment_detail_report_title),
        description = stringResource(R.string.comment_detail_report_description),
        negativeText = stringResource(R.string.common_negative),
        positiveText = stringResource(R.string.comment_detail_report),
        onDismiss = { onIntent(dismissAction) },
        onClickNegative = { onIntent(dismissAction) },
        onClickPositive = {
            onIntent(dismissAction)
            onIntent(CommentDetailIntent.CommentReportClick(comment))
        },
    )
}
