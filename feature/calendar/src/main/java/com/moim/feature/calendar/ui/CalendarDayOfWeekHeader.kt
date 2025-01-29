package com.moim.feature.calendar.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.calendar.util.displayText
import java.time.DayOfWeek

@Composable
fun CalendarDayOfWeekHeader(
    modifier: Modifier = Modifier,
    daysOfWeek: List<DayOfWeek>,
    selectedDayOfWeek: DayOfWeek? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        daysOfWeek.forEach { dayOfWeek ->
            MoimText(
                modifier = Modifier.weight(1f),
                text = dayOfWeek.displayText(),
                textAlign = TextAlign.Center,
                singleLine = false,
                style = MoimTheme.typography.body01.medium,
                color = if (selectedDayOfWeek == dayOfWeek) MoimTheme.colors.primary.primary else MoimTheme.colors.gray.gray05,
            )
        }
    }
}