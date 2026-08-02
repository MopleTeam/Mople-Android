package com.moim.core.remote.datasource.holiday

import com.moim.core.remote.model.HolidayResponse

interface HolidayRemoteDataSource {
    suspend fun getHolidays(year: String): List<HolidayResponse>
}
