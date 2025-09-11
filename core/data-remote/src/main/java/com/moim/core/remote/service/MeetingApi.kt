package com.moim.core.remote.service

import com.moim.core.remote.model.MeetingResponse
import com.moim.core.remote.model.PaginationContainerResponse
import com.moim.core.remote.model.UserResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MeetingApi {

    @GET("meet/list")
    suspend fun getMeetings(
        @Query("cursor") cursor: String,
        @Query("size") size: Int,
    ): PaginationContainerResponse<List<MeetingResponse>>

    @GET("meet/{meetId}")
    suspend fun getMeeting(@Path("meetId") id: String): MeetingResponse

    @POST("meet/invite/{meetId}")
    suspend fun getMeetingInviteCode(@Path("meetId") id: String): String

    @GET("/meet/members/{meetId}")
    suspend fun getMeetingParticipants(
        @Path("meetId") id: String,
        @Query("cursor") cursor: String,
        @Query("size") size: Int,
    ): PaginationContainerResponse<List<UserResponse>>

    @POST("meet/create")
    suspend fun createMeeting(
        @Body params: JsonObject
    ): MeetingResponse

    @PATCH("meet/update/{meetId}")
    suspend fun updateMeeting(
        @Path("meetId") id: String,
        @Body params: JsonObject
    ): MeetingResponse

    @POST("meet/join/{meetCode}")
    suspend fun joinMeeting(
        @Path("meetCode") meetCode: String
    ): MeetingResponse

    @DELETE("meet/{meetId}")
    suspend fun deleteMeeting(@Path("meetId") id: String)
}