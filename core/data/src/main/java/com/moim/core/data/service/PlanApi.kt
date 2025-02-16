package com.moim.core.data.service

import com.moim.core.datamodel.MeetingPlanContainerResponse
import com.moim.core.datamodel.ParticipantContainerResponse
import com.moim.core.datamodel.PlanResponse
import com.moim.core.datamodel.PlanReviewContainerResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface PlanApi {

    @GET("/plan/view")
    suspend fun getCurrentPlan(): MeetingPlanContainerResponse

    @GET("/plan/list/{meetId}")
    suspend fun getPlans(@Path("meetId") id: String): List<PlanResponse>

    @GET("/plan/page")
    suspend fun getPlansForCalendar(
        @Query("date") date: String,
    ): PlanReviewContainerResponse

    @GET("/plan/detail/{planId}")
    suspend fun getPlan(@Path("planId") planId: String): PlanResponse

    @GET("/plan/participants/{planId}")
    suspend fun getPlanParticipants(@Path("planId") planId: String) : ParticipantContainerResponse

    @POST("/plan/create")
    suspend fun createPlan(@Body params: JsonObject): PlanResponse

    @PATCH("/plan/update")
    suspend fun updatePlan(@Body params: JsonObject): PlanResponse

    @POST("/plan/report")
    suspend fun reportPlan(@Body params: JsonObject)

    @DELETE("plan/{planId}")
    suspend fun deletePlan(@Path("planId") id: String)
}