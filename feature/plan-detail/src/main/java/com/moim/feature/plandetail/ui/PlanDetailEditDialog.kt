package com.moim.feature.plandetail.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.moim.core.common.model.Comment
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimAlertDialog
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.moimButtomColors
import com.moim.feature.plandetail.OnPlanDetailUiAction
import com.moim.feature.plandetail.PlanDetailUiAction

@Composable
fun PlanDetailEditDialog(
    onUiAction: OnPlanDetailUiAction
) {
    val dismissAction = PlanDetailUiAction.OnShowPlanEditDialog(false)

    MoimAlertDialog(
        title = stringResource(R.string.plan_detail_edit),
        description = stringResource(R.string.plan_detail_update_title, stringResource(R.string.plan_detail_plan)),
        negativeText = stringResource(R.string.plan_detail_delete),
        positiveText = stringResource(R.string.plan_detail_update),
        onDismiss = { onUiAction(dismissAction) },
        onClickNegative = {
            onUiAction(dismissAction)
            onUiAction(PlanDetailUiAction.OnClickPlanDelete)
        },
        onClickPositive = {
            onUiAction(dismissAction)
            onUiAction(PlanDetailUiAction.OnClickPlanUpdate)
        },
    )
}

@Composable
fun PlanDetailCommentEditDialog(
    comment: Comment,
    onUiAction: OnPlanDetailUiAction
) {
    val dismissAction = PlanDetailUiAction.OnShowCommentEditDialog(false, null)

    MoimAlertDialog(
        title = stringResource(R.string.plan_detail_edit),
        description = stringResource(R.string.plan_detail_update_title, stringResource(R.string.plan_detail_comment)),
        negativeText = stringResource(R.string.plan_detail_delete),
        positiveText = stringResource(R.string.plan_detail_update),
        onDismiss = { onUiAction(dismissAction) },
        onClickNegative = {
            onUiAction(dismissAction)
            onUiAction(PlanDetailUiAction.OnClickCommentDelete(comment))
        },
        onClickPositive = {
            onUiAction(dismissAction)
            onUiAction(PlanDetailUiAction.OnClickCommentUpdate(comment))
        },
    )
}

@Composable
fun PlanDetailReportDialog(
    onUiAction: OnPlanDetailUiAction
) {
    val dismissAction = PlanDetailUiAction.OnShowPlanReportDialog(false)

    MoimAlertDialog(
        title = stringResource(R.string.plan_detail_report_title, stringResource(R.string.plan_detail_plan)),
        description = stringResource(R.string.plan_detail_report_description),
        negativeText = stringResource(R.string.common_negative),
        positiveText = stringResource(R.string.plan_detail_report),
        positiveButtonColors = moimButtomColors().copy(containerColor = MoimTheme.colors.secondary),
        onDismiss = { onUiAction(dismissAction) },
        onClickNegative = { onUiAction(dismissAction) },
        onClickPositive = {
            onUiAction(dismissAction)
            onUiAction(PlanDetailUiAction.OnClickPlanReport)
        },
    )
}

@Composable
fun PlanDetailCommentReportDialog(
    comment: Comment,
    onUiAction: OnPlanDetailUiAction
) {
    val dismissAction = PlanDetailUiAction.OnShowCommentReportDialog(false, null)

    MoimAlertDialog(
        title = stringResource(R.string.plan_detail_report_title, stringResource(R.string.plan_detail_comment)),
        description = stringResource(R.string.plan_detail_report_description),
        negativeText = stringResource(R.string.common_negative),
        positiveText = stringResource(R.string.plan_detail_report),
        onDismiss = { onUiAction(dismissAction) },
        onClickNegative = { onUiAction(dismissAction) },
        onClickPositive = {
            onUiAction(dismissAction)
            onUiAction(PlanDetailUiAction.OnClickCommentReport(comment))
        },
    )
}