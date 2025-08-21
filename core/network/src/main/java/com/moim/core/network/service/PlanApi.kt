package com.moim.core.network.service

import com.moim.core.datamodel.MeetingPlanContainerResponse
import com.moim.core.datamodel.PaginationContainerResponse
import com.moim.core.datamodel.PlanResponse
import com.moim.core.datamodel.PlanReviewContainerResponse
import com.moim.core.datamodel.UserResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PlanApi {

    @GET("plan/view")
    suspend fun getCurrentPlan(): MeetingPlanContainerResponse

    @GET("plan/list/{meetId}")
    suspend fun getPlans(
        @Path("meetId") id: String,
        @Query("cursor") cursor: String,
        @Query("size") size: Int,
    ): PaginationContainerResponse<List<PlanResponse>>

    @GET("plan/page")
    suspend fun getPlansForCalendar(
        @Query("date") date: String,
    ): PlanReviewContainerResponse

    @GET("plan/detail/{planId}")
    suspend fun getPlan(@Path("planId") planId: String): PlanResponse

    @GET("plan/participants/{planId}")
    suspend fun getPlanParticipants(
        @Path("planId") planId: String,
        @Query("cursor") cursor: String,
        @Query("size") size: Int,
    ) : PaginationContainerResponse<List<UserResponse>>

    @POST("plan/join/{planId}")
    suspend fun joinPlan(@Path("planId") id: String)

    @DELETE("plan/leave/{planId}")
    suspend fun leavePlan(@Path("planId") id: String)

    @POST("plan/create")
    suspend fun createPlan(@Body params: JsonObject): PlanResponse

    @PATCH("plan/update")
    suspend fun updatePlan(@Body params: JsonObject): PlanResponse

    @POST("plan/report")
    suspend fun reportPlan(@Body params: JsonObject)

    @DELETE("plan/{planId}")
    suspend fun deletePlan(@Path("planId") id: String)
}