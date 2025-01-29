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
    titleText: String,
    hintText: String,
    valueText: String? = null,
    @DrawableRes iconRes: Int? = null,
    enable: Boolean = true,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        MoimText(
            text = titleText,
            style = MoimTheme.typography.title03.semiBold,
            color = MoimTheme.colors.gray.gray01
        )
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MoimTheme.colors.bg.input)
                .onSingleClick(
                    onClick = onClick,
                    enabled = enable
                )
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconRes != null) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = ImageVector.vectorResource(iconRes),
                    contentDescription = "",
                    tint = MoimTheme.colors.icon
                )
                Spacer(Modifier.width(16.dp))
            }

            MoimText(
                modifier = Modifier.fillMaxWidth(),
                text = if (valueText.isNullOrEmpty()) hintText else valueText,
                style = MoimTheme.typography.body01.regular,
                color = if (valueText.isNullOrEmpty()) MoimTheme.colors.gray.gray05 else MoimTheme.colors.gray.gray01,
            )
        }
    }
}

@Preview
@Composable
private fun PlanWriteSelectedBoxPreview() {
    MoimTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MoimTheme.colors.white)
                .padding(20.dp)
        ) {
            PlanWriteSelectedBox(
                titleText = "날짜 선택",
                hintText = "날짜를 선택해주세요",
                iconRes = R.drawable.ic_calendar,
                onClick = {},
            )
        }
    }
}