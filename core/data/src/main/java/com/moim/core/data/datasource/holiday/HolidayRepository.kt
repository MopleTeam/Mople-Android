package com.moim.core.data.datasource.holiday

import com.moim.core.common.model.Holiday
import java.time.ZonedDateTime

interface HolidayRepository {
    suspend fun getHolidays(currentYear: ZonedDateTime): List<Holiday>
}
