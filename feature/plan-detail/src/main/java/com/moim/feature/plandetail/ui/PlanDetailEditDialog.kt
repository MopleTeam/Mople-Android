package com.moim.feature.plandetail.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.moim.core.common.model.Comment
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimAlertDialog
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.moimButtomColors
import com.moim.feature.plandetail.OnPlanDetailIntent
import com.moim.feature.plandetail.model.PlanDetailIntent

@Composable
fun PlanDetailEditDialog(onIntent: OnPlanDetailIntent) {
    val dismissIntent = PlanDetailIntent.PlanEditDialogShow(false)

    MoimAlertDialog(
        title = stringResource(R.string.plan_detail_edit),
        description = stringResource(R.string.plan_detail_update_title, stringResource(R.string.plan_detail_plan)),
        negativeText = stringResource(R.string.plan_detail_delete),
        positiveText = stringResource(R.string.plan_detail_update),
        onDismiss = { onIntent(dismissIntent) },
        onClickNegative = {
            onIntent(dismissIntent)
            onIntent(PlanDetailIntent.PlanDeleteClick)
        },
        onClickPositive = {
            onIntent(dismissIntent)
            onIntent(PlanDetailIntent.PlanUpdateClick)
        },
    )
}

@Composable
fun PlanDetailCommentEditDialog(
    comment: Comment,
    onIntent: OnPlanDetailIntent,
) {
    val dismissIntent = PlanDetailIntent.CommentEditDialogShow(false, null)

    MoimAlertDialog(
        title = stringResource(R.string.plan_detail_edit),
        description = stringResource(R.string.plan_detail_update_title, stringResource(R.string.plan_detail_comment)),
        negativeText = stringResource(R.string.plan_detail_delete),
        positiveText = stringResource(R.string.plan_detail_update),
        onDismiss = { onIntent(dismissIntent) },
        onClickNegative = {
            onIntent(dismissIntent)
            onIntent(PlanDetailIntent.CommentDeleteClick(comment))
        },
        onClickPositive = {
            onIntent(dismissIntent)
            onIntent(PlanDetailIntent.CommentUpdateClick(comment))
        },
    )
}

@Composable
fun PlanDetailReportDialog(onIntent: OnPlanDetailIntent) {
    val dismissIntent = PlanDetailIntent.PlanReportDialogShow(false)

    MoimAlertDialog(
        title = stringResource(R.string.plan_detail_report_title, stringResource(R.string.plan_detail_plan)),
        description = stringResource(R.string.plan_detail_report_description),
        negativeText = stringResource(R.string.common_negative),
        positiveText = stringResource(R.string.plan_detail_report),
        positiveButtonColors = moimButtomColors().copy(containerColor = MoimTheme.colors.secondary),
        onDismiss = { onIntent(dismissIntent) },
        onClickNegative = { onIntent(dismissIntent) },
        onClickPositive = {
            onIntent(dismissIntent)
            onIntent(PlanDetailIntent.PlanReportClick)
        },
    )
}

@Composable
fun PlanDetailCommentReportDialog(
    comment: Comment,
    onIntent: OnPlanDetailIntent,
) {
    val dismissIntent = PlanDetailIntent.CommentReportDialogShow(false, null)

    MoimAlertDialog(
        title = stringResource(R.string.plan_detail_report_title, stringResource(R.string.plan_detail_comment)),
        description = stringResource(R.string.plan_detail_report_description),
        negativeText = stringResource(R.string.common_negative),
        positiveText = stringResource(R.string.plan_detail_report),
        onDismiss = { onIntent(dismissIntent) },
        onClickNegative = { onIntent(dismissIntent) },
        onClickPositive = {
            onIntent(dismissIntent)
            onIntent(PlanDetailIntent.CommentReportClick(comment))
        },
    )
}
