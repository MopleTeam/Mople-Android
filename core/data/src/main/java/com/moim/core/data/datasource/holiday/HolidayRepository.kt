package com.moim.core.data.datasource.holiday

import com.moim.core.common.model.Holiday
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface HolidayRepository {

    fun getHolidays(currentYear: ZonedDateTime): Flow<List<Holiday>>
}