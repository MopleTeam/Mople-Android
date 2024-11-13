package com.moim.core.data.service

import com.moim.core.data.model.MeetingPlanResponse
import com.moim.core.data.model.MeetingResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface MeetingApi {

    @GET("meeting/active")
    suspend fun getMeetings(): List<MeetingResponse>

    @GET("meeting/{id}")
    suspend fun getMeeting(@Path("id") id: String): MeetingResponse

    @GET("meeting/plan")
    suspend fun getMeetingPlans(
        @Query("page") page: Int,
        @Query("yearMonth") yearMonth: String,
        @Query("closed") isClosed: Boolean,
    ): List<MeetingPlanResponse>

    @POST("/meeting/create")
    suspend fun createMeeting(
        @Body params: JsonObject
    ): MeetingResponse

    @PATCH("/meeting/{id}")
    suspend fun updateMeeting(
        @Path("id") id: String,
        @Body params: JsonObject
    ): MeetingResponse
}