package com.moim.core.remote.service

import com.moim.core.remote.model.HolidayResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface HolidayApi {
    @GET("holiday")
    suspend fun getHolidays(
        @Query("year") year: String,
    ): List<HolidayResponse>
}
