package com.moim.feature.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.moim.core.common.util.default
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.calendar.CalendarUiAction
import com.moim.feature.calendar.OnCalendarUiAction
import java.time.ZonedDateTime

@Composable
fun CalendarDay(
    modifier: Modifier = Modifier,
    day: ZonedDateTime,
    selectedDay: ZonedDateTime? = null,
    isCurrentDatePosition: Boolean = true,
    enabled: Boolean = false,
    onUiAction: OnCalendarUiAction = {}
) {
    val isToday = (day == ZonedDateTime.now().default())
    val isSelected = (day == selectedDay)
    val textColor = when {
        enabled.not() && isCurrentDatePosition -> MoimTheme.colors.gray.gray07
        enabled && isCurrentDatePosition && isSelected -> MoimTheme.colors.primary.primary
        enabled && isCurrentDatePosition -> MoimTheme.colors.gray.gray01
        else -> MoimTheme.colors.white
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(100))
            .onSingleClick(
                enabled = enabled,
                onClick = { onUiAction(CalendarUiAction.OnClickDateDay(day)) }
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .aspectRatio(1f)
                    .padding(4.dp)
                    .background(MoimTheme.colors.primary.primary.copy(alpha = 0.1f), RoundedCornerShape(100))
            )
        } else if (isToday) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .aspectRatio(1f)
                    .padding(4.dp)
                    .background(MoimTheme.colors.bg.primary, RoundedCornerShape(100))
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .defaultMinSize(minHeight = 48.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = day.dayOfMonth.toString(),
                textAlign = TextAlign.Center,
                style = MoimTheme.typography.title03.semiBold,
                color = textColor
            )
        }
    }
}
