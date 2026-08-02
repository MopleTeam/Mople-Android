package com.moim.core.data.datasource.holiday

import com.moim.core.common.model.Holiday
import com.moim.core.remote.datasource.holiday.HolidayRemoteDataSource
import com.moim.core.remote.model.HolidayResponse
import com.moim.core.remote.model.asItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.ZonedDateTime
import javax.inject.Inject

internal class HolidayRepositoryImpl @Inject constructor(
    private val holidayRemoteDataSource: HolidayRemoteDataSource,
) : HolidayRepository {
    override fun getHolidays(currentYear: ZonedDateTime): Flow<List<Holiday>> =
        flow {
            emit(holidayRemoteDataSource.getHolidays(year = currentYear.year.toString()).map(HolidayResponse::asItem))
        }
}
