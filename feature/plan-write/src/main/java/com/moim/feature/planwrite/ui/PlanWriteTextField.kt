package com.moim.feature.planwrite.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.MoimTextField
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.planwrite.OnPlanWriteUiAction
import com.moim.feature.planwrite.PlanWriteUiAction

@Composable
fun PlanWriteTextField(
    modifier: Modifier = Modifier,
    planName: String,
    onUiAction: OnPlanWriteUiAction
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        MoimText(
            text = stringResource(R.string.plan_write_name),
            style = MoimTheme.typography.title03.semiBold,
            color = MoimTheme.colors.gray.gray01
        )
        Spacer(Modifier.height(8.dp))

        MoimTextField(
            modifier = Modifier.fillMaxWidth(),
            text = planName,
            textMaxLength = 30,
            hintText = stringResource(R.string.plan_write_name_hint),
            onTextChanged = { onUiAction(PlanWriteUiAction.OnChangePlanName(it)) }
        )
    }
}