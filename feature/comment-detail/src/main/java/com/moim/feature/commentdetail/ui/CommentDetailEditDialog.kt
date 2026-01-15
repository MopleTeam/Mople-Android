package com.moim.feature.commentdetail.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.moim.core.common.model.Comment
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimAlertDialog
import com.moim.feature.commentdetail.CommentDetailAction

@Composable
fun CommentDetailEditDialog(
    comment: Comment,
    onUiAction: (CommentDetailAction) -> Unit,
) {
    val dismissAction = CommentDetailAction.OnShowCommentEditDialog(false, null)

    MoimAlertDialog(
        title = stringResource(R.string.comment_detail_edit),
        description = stringResource(R.string.comment_detail_update_title),
        negativeText = stringResource(R.string.comment_detail_delete),
        positiveText = stringResource(R.string.comment_detail_update),
        onDismiss = { onUiAction(dismissAction) },
        onClickNegative = {
            onUiAction(dismissAction)
            onUiAction(CommentDetailAction.OnClickCommentDelete(comment))
        },
        onClickPositive = {
            onUiAction(dismissAction)
            onUiAction(CommentDetailAction.OnClickCommentUpdate(comment))
        },
    )
}

@Composable
fun CommentDetailReportDialog(
    comment: Comment,
    onUiAction: (CommentDetailAction) -> Unit,
) {
    val dismissAction = CommentDetailAction.OnShowCommentReportDialog(false, null)

    MoimAlertDialog(
        title = stringResource(R.string.comment_detail_report_title),
        description = stringResource(R.string.comment_detail_report_description),
        negativeText = stringResource(R.string.common_negative),
        positiveText = stringResource(R.string.comment_detail_report),
        onDismiss = { onUiAction(dismissAction) },
        onClickNegative = { onUiAction(dismissAction) },
        onClickPositive = {
            onUiAction(dismissAction)
            onUiAction(CommentDetailAction.OnClickCommentReport(comment))
        },
    )
}
