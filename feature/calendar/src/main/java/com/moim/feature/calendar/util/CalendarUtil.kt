package com.moim.feature.calendar.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import com.kizitonwose.calendar.compose.CalendarLayoutInfo
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.Week
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

fun DayOfWeek.displayText(uppercase: Boolean = false): String =
    getDisplayName(TextStyle.SHORT, Locale.KOREA).let { value ->
        if (uppercase) value.uppercase(Locale.KOREA) else value
    }

@Composable
fun rememberFirstMostVisibleMonth(
    state: CalendarState,
    viewportPercent: Float = 50f,
): CalendarMonth {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth) }

    LaunchedEffect(state) {
        snapshotFlow { state.layoutInfo.firstMostVisibleMonth(viewportPercent) }
            .filterNotNull()
            .collect { month -> visibleMonth.value = month }
    }

    return visibleMonth.value
}

@Composable
fun rememberFirstVisibleWeekAfterScroll(state: WeekCalendarState): Week {
    val visibleWeek = remember(state) { mutableStateOf(state.firstVisibleWeek) }

    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }
            .filter { scrolling -> !scrolling }
            .collect { visibleWeek.value = state.firstVisibleWeek }
    }

    return visibleWeek.value
}

private fun CalendarLayoutInfo.firstMostVisibleMonth(viewportPercent: Float = 50f): CalendarMonth? =
    if (visibleMonthsInfo.isEmpty()) {
        null
    } else {
        val viewportSize = (viewportEndOffset + viewportStartOffset) * viewportPercent / 100f
        visibleMonthsInfo
            .firstOrNull { itemInfo ->
                if (itemInfo.offset < 0) {
                    itemInfo.offset + itemInfo.size >= viewportSize
                } else {
                    itemInfo.size - itemInfo.offset >= viewportSize
                }
            }?.month
    }
