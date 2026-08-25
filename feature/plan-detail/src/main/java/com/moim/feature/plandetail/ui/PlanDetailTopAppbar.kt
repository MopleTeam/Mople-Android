package com.moim.feature.plandetail.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimIconButton
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.feature.plandetail.OnPlanDetailIntent
import com.moim.feature.plandetail.model.PlanDetailIntent

@Composable
fun PlanDetailTopAppbar(
    modifier: Modifier = Modifier,
    isMyPlan: Boolean,
    onIntent: OnPlanDetailIntent,
) {
    MoimTopAppbar(
        modifier = modifier,
        title = stringResource(R.string.plan_detail_title),
        onClickNavigate = { onIntent(PlanDetailIntent.BackClick) },
        actions = {
            MoimIconButton(
                iconRes = R.drawable.ic_more_bold,
                onClick = {
                    if (isMyPlan) {
                        onIntent(PlanDetailIntent.PlanEditDialogShow(true))
                    } else {
                        onIntent(PlanDetailIntent.PlanReportDialogShow(true))
                    }
                },
            )
        },
    )
}
