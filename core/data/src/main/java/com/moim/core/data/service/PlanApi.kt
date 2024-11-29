package com.moim.core.data.service

import com.moim.core.data.model.MeetingPlanContainer
import com.moim.core.data.model.PlanResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface PlanApi {

    @GET("/plan/view")
    suspend fun getCurrentPlan(): MeetingPlanContainer

    @GET("/plan/list/{meetId}")
    suspend fun getPlans(@Path("meetId") id: String): List<PlanResponse>

    @GET("/plan/plans")
    suspend fun getPlansForCalendar(
        @Query("page") page: Int,
        @Query("yearMonth") yearMonth: String,
        @Query("closed") isClosed: Boolean,
    ): List<PlanResponse>

    @GET("/plan/detail/{planId}")
    suspend fun getPlan(@Path("planId") planId: String): PlanResponse

    @POST("/plan/create")
    suspend fun createPlan(@Body params: JsonObject): PlanResponse
}