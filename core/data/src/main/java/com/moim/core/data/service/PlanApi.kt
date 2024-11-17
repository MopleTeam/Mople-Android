package com.moim.core.data.service

import com.moim.core.data.model.MeetingPlanContainer
import com.moim.core.data.model.MeetingPlanResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface PlanApi {

    @GET("/plan/view")
    suspend fun getCurrentPlan() : MeetingPlanContainer

    @GET("/plan/plans")
    suspend fun getPlans(
        @Query("page") page: Int,
        @Query("yearMonth") yearMonth: String,
        @Query("closed") isClosed: Boolean,
    ): List<MeetingPlanResponse>

    @GET("/meeting/plan/{id}")
    suspend fun getPlan(@Path("id") planId: String): MeetingPlanResponse
}