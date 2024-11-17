package com.moim.core.data.service

import com.moim.core.data.model.MeetingResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

internal interface MeetingApi {

    @GET("meet/list")
    suspend fun getMeetings(): List<MeetingResponse>

    @GET("meet/{meetId}")
    suspend fun getMeeting(@Path("meetId") id: String): MeetingResponse

    @POST("/meet/create")
    suspend fun createMeeting(
        @Body params: JsonObject
    ): MeetingResponse

    @PATCH("/meet/{meetId}")
    suspend fun updateMeeting(
        @Path("meetId") id: String,
        @Body params: JsonObject
    ): MeetingResponse
}