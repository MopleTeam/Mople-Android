package com.moim.core.remote.datasource.holiday

import com.moim.core.remote.di.qualifiers.MoimApi
import com.moim.core.remote.model.HolidayResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

internal class HolidayRemoteDataSourceImpl @Inject constructor(
    @MoimApi private val client: HttpClient,
) : HolidayRemoteDataSource {
    override suspend fun getHolidays(year: String): List<HolidayResponse> =
        client
            .get("holiday") {
                parameter("year", year)
            }.body()
}
