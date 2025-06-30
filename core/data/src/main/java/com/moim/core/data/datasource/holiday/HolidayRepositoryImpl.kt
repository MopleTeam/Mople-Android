package com.moim.core.data.datasource.holiday

import com.moim.core.data.util.catchFlow
import com.moim.core.datamodel.HolidayResponse
import com.moim.core.model.Holiday
import com.moim.core.model.asItem
import com.moim.core.network.service.HolidayApi
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime
import javax.inject.Inject

internal class HolidayRepositoryImpl @Inject constructor(
    private val holidayApi: HolidayApi,
) : HolidayRepository {

    override fun getHolidays(currentYear: ZonedDateTime): Flow<List<Holiday>> = catchFlow {
        emit(holidayApi.getHolidays(year = currentYear.year.toString()).map(HolidayResponse::asItem))
    }
}