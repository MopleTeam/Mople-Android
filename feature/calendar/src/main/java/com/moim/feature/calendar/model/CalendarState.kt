package com.moim.feature.calendar.model

import androidx.compose.runtime.Immutable
import com.kizitonwose.calendar.core.daysOfWeek
import com.moim.core.common.model.item.PlanItem
import com.moim.core.common.result.Result
import com.moim.core.common.util.default
import java.time.DayOfWeek
import java.time.ZonedDateTime

@Immutable
data class CalendarState(
    val plans: Result<List<PlanItem>> = Result.Loading,
    val selectDayOfMonth: ZonedDateTime = ZonedDateTime.now().default().withDayOfMonth(1),
    val selectDay: ZonedDateTime? = null,
    val loadDates: List<ZonedDateTime> = listOf(selectDayOfMonth),
    val daysOfWeek: List<DayOfWeek> = daysOfWeek(),
    val holidays: List<ZonedDateTime> = emptyList(),
    val isExpandable: Boolean = true,
) {
    val isSuccess
        get() = plans is Result.Success

    val isError
        get() = plans is Result.Error

    val isLoading
        get() = !isSuccess && !isError
}
