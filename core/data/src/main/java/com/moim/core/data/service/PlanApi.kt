package com.moim.core.data.service

import com.moim.core.data.model.MeetingPlanResponse
import retrofit2.http.GET
import retrofit2.http.Path

internal interface PlanApi {

    @GET("/plan/view")
    suspend fun getCurrentPlan()

    @GET("/meeting/plan/{id}")
    suspend fun getPlan(@Path("id") planId: String): MeetingPlanResponse
}