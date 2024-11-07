package com.moim.core.data.service

import com.moim.core.data.model.MeetingPlanResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface MeetingApi {

    @GET("meeting/plan")
    suspend fun getMeetingPlans(
        @Query("page") page: Int,
        @Query("yearMonth") yearMonth: String,
        @Query("closed") isClosed: Boolean,
    ): List<MeetingPlanResponse>
}