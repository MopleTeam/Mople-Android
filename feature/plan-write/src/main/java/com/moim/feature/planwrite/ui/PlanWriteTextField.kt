package com.moim.feature.planwrite.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.MoimTextField
import com.moim.core.designsystem.theme.MoimTheme

@Composable
fun PlanWriteTextField(
    modifier: Modifier = Modifier,
    value: String,
    title: String,
    hint: String,
    isSingleLine: Boolean = true,
    maxLength: Int = 30,
    titleOption: String? = null,
    onTextChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MoimText(
                text = title,
                style = MoimTheme.typography.title03.semiBold,
                color = MoimTheme.colors.text.text01,
            )

            if (titleOption != null) {
                Spacer(Modifier.width(4.dp))
                MoimText(
                    text = titleOption,
                    style = MoimTheme.typography.body01.regular,
                    color = MoimTheme.colors.text.text03,
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        Box {
            MoimTextField(
                modifier = modifier.fillMaxWidth(),
                text = value,
                textMaxLength = maxLength,
                hintText = hint,
                singleLine = isSingleLine,
                onTextChanged = onTextChange,
            )

            if (!isSingleLine) {
                MoimText(
                    modifier =
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 18.dp),
                    text = "${value.length}/$maxLength",
                    style = MoimTheme.typography.body01.regular,
                    color = MoimTheme.colors.text.text04,
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun PlanWriteTextFieldPreview() {
    MoimTheme {
        Column(
            modifier = Modifier.padding(20.dp),
        ) {
            PlanWriteTextField(
                title = stringResource(R.string.plan_write_name),
                hint = stringResource(R.string.plan_write_name_hint),
                value = "",
                onTextChange = {},
            )

            Spacer(Modifier.height(20.dp))

            PlanWriteTextField(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 144.dp),
                title = stringResource(R.string.plan_write_plan_info),
                titleOption = stringResource(R.string.plan_write_select_option),
                hint = stringResource(R.string.plan_write_plan_info_hint),
                maxLength = 100,
                isSingleLine = false,
                value = "모임 정보는 공백 포함 100자로 부탁드립니다.모임 정보는 공백 포함 100자로 부탁드립니다.모임 정보는 공백 포함 100자로 부탁드립니다.",
                onTextChange = {},
            )
        }
    }
}
