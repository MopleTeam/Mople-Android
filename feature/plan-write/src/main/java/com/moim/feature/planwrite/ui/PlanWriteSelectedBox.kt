package com.moim.feature.planwrite.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme

@Composable
fun PlanWriteSelectedBox(
    modifier: Modifier = Modifier,
    title: String,
    hint: String,
    titleOption: String? = null,
    value: String? = null,
    @DrawableRes iconRes: Int? = null,
    enable: Boolean = true,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MoimText(
                text = title,
                style = MoimTheme.typography.title03.semiBold,
                color = MoimTheme.colors.gray.gray01,
            )

            if (titleOption != null) {
                Spacer(Modifier.width(4.dp))
                MoimText(
                    text = titleOption,
                    style = MoimTheme.typography.body01.regular,
                    color = MoimTheme.colors.gray.gray04,
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (enable) MoimTheme.colors.bg.input else MoimTheme.colors.input.disable)
                    .onSingleClick(
                        onClick = onClick,
                        enabled = enable,
                    ).padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (iconRes != null) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = ImageVector.vectorResource(iconRes),
                    contentDescription = "",
                    tint = if (enable) MoimTheme.colors.icon else MoimTheme.colors.gray.gray06,
                )
                Spacer(Modifier.width(16.dp))
            }

            MoimText(
                modifier = Modifier.fillMaxWidth(),
                text = if (value.isNullOrEmpty()) hint else value,
                style = MoimTheme.typography.body01.regular,
                color = if (value.isNullOrEmpty()) MoimTheme.colors.gray.gray05 else MoimTheme.colors.gray.gray01,
            )
        }
    }
}

@Preview
@Composable
private fun PlanWriteSelectedBoxPreview() {
    MoimTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(MoimTheme.colors.white)
                    .padding(20.dp),
        ) {
            PlanWriteSelectedBox(
                title = "날짜 선택",
                titleOption = "(선택)",
                hint = "날짜를 선택해주세요",
                enable = false,
                iconRes = R.drawable.ic_calendar,
                onClick = {},
            )
        }
    }
}
