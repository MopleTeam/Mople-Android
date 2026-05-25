package com.moim.feature.meetingdetail.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.meetingdetail.MeetingDetailUiAction

private const val CHIP_ANIMATION_DURATION = 250

@Composable
fun MeetingDetailHeader(
    modifier: Modifier = Modifier,
    isSelectedFuturePlan: Boolean = true,
    onUiAction: (MeetingDetailUiAction) -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(MoimTheme.colors.bg.secondary)
                .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MeetingDetailChipContainer(
                isSelectedFuturePlan = isSelectedFuturePlan,
                onUiAction = onUiAction,
            )
        }
    }
}

@Composable
private fun MeetingDetailChipContainer(
    isSelectedFuturePlan: Boolean,
    onUiAction: (MeetingDetailUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val spacerWidthPx = with(density) { 8.dp.roundToPx() }

    var pastChipWidth by remember { mutableIntStateOf(0) }
    var futureChipWidth by remember { mutableIntStateOf(0) }
    val hasMeasured = pastChipWidth > 0 && futureChipWidth > 0

    Box(
        modifier =
            modifier
                .padding(6.dp)
                .height(IntrinsicSize.Max),
    ) {
        if (hasMeasured) {
            val targetX = if (isSelectedFuturePlan) 0 else futureChipWidth + spacerWidthPx
            val targetWidth = if (isSelectedFuturePlan) futureChipWidth else pastChipWidth

            val animatedX by animateIntAsState(
                targetValue = targetX,
                animationSpec = tween(durationMillis = CHIP_ANIMATION_DURATION),
                label = "chipIndicatorX",
            )
            val animatedWidth by animateIntAsState(
                targetValue = targetWidth,
                animationSpec = tween(durationMillis = CHIP_ANIMATION_DURATION),
                label = "chipIndicatorWidth",
            )

            Box(
                modifier =
                    Modifier
                        .offset { IntOffset(animatedX, 0) }
                        .width(with(density) { animatedWidth.toDp() })
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MoimTheme.colors.global.primary),
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            MeetingDetailChip(
                modifier = Modifier.onSizeChanged { futureChipWidth = it.width },
                text = stringResource(R.string.meeting_detail_future_plan),
                isSelected = isSelectedFuturePlan,
                onClick = { onUiAction(MeetingDetailUiAction.OnClickPlanTab(true)) },
            )

            Spacer(Modifier.width(8.dp))

            MeetingDetailChip(
                modifier = Modifier.onSizeChanged { pastChipWidth = it.width },
                text = stringResource(R.string.meeting_detail_past_plan),
                isSelected = !isSelectedFuturePlan,
                onClick = { onUiAction(MeetingDetailUiAction.OnClickPlanTab(false)) },
            )
        }
    }
}

@Composable
private fun MeetingDetailChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val textColor by animateColorAsState(
        targetValue =
            if (isSelected) {
                MoimTheme.colors.text.primary
            } else {
                MoimTheme.colors.text.text03
            },
        animationSpec = tween(durationMillis = CHIP_ANIMATION_DURATION),
        label = "chipTextColor",
    )

    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onClick)
                .padding(
                    vertical = 8.dp,
                    horizontal = 20.dp,
                ),
        contentAlignment = Alignment.Center,
    ) {
        MoimText(
            text = text,
            style = MoimTheme.typography.body01.semiBold,
            color = textColor,
        )
    }
}

@ThemePreviews
@Composable
private fun MeetingDetailHeaderPreview() {
    MoimTheme {
        MeetingDetailHeader(
            isSelectedFuturePlan = true,
            onUiAction = {},
        )
    }
}
