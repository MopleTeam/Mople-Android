package com.moim.feature.calendar.model

import com.moim.core.common.model.ViewIdType
import com.moim.core.ui.mvi.Intent
import java.time.ZonedDateTime

sealed interface CalendarIntent : Intent {
    data object RefreshClick : CalendarIntent

    data class DateDayClick(
        val date: ZonedDateTime,
    ) : CalendarIntent

    data class ExpandableClick(
        val date: ZonedDateTime,
    ) : CalendarIntent

    data class MeetingPlanClick(
        val viewIdType: ViewIdType,
    ) : CalendarIntent

    data class DateChange(
        val date: ZonedDateTime,
    ) : CalendarIntent
}
