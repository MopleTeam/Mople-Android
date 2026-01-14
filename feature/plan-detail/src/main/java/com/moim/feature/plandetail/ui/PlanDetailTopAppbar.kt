package com.moim.feature.plandetail.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimIconButton
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.feature.plandetail.OnPlanDetailUiAction
import com.moim.feature.plandetail.PlanDetailUiAction

@Composable
fun PlanDetailTopAppbar(
    modifier: Modifier = Modifier,
    isMyPlan: Boolean,
    onUiAction: OnPlanDetailUiAction,
) {
    MoimTopAppbar(
        modifier = modifier,
        title = stringResource(R.string.plan_detail_title),
        onClickNavigate = { onUiAction(PlanDetailUiAction.OnClickBack) },
        actions = {
            MoimIconButton(
                iconRes = R.drawable.ic_more_bold,
                onClick = {
                    if (isMyPlan) {
                        onUiAction(PlanDetailUiAction.OnShowPlanEditDialog(true))
                    } else {
                        onUiAction(PlanDetailUiAction.OnShowPlanReportDialog(true))
                    }
                },
            )
        },
    )
}
