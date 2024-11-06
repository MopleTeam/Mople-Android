package com.moim.core.data.service

import com.moim.core.data.model.MeetingResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface MeetingApi {

    @GET("meeting/plan")
    suspend fun getMeetings(
        @Query("page") page: Int,
        @Query("yearMonth") yearMonth: String,
        @Query("closed") isClosed: Boolean,
    ): List<MeetingResponse>
}