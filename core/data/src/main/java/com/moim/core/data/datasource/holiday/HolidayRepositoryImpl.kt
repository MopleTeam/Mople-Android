package com.moim.core.data.datasource.holiday

import com.moim.core.common.model.Holiday
import com.moim.core.remote.datasource.holiday.HolidayRemoteDataSource
import com.moim.core.remote.model.HolidayResponse
import com.moim.core.remote.model.asItem
import java.time.ZonedDateTime
import javax.inject.Inject

internal class HolidayRepositoryImpl @Inject constructor(
    private val holidayRemoteDataSource: HolidayRemoteDataSource,
) : HolidayRepository {
    override suspend fun getHolidays(currentYear: ZonedDateTime): List<Holiday> =
        holidayRemoteDataSource.getHolidays(year = currentYear.year.toString()).map(HolidayResponse::asItem)
}
