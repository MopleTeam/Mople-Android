package com.moim.core.remote.service

import com.moim.core.remote.model.NotificationResponse
import com.moim.core.remote.model.PaginationContainerResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface NotificationApi {
    @GET("notification/list")
    suspend fun getNotifications(
        @Query("cursor") cursor: String,
        @Query("size") size: Int,
    ): PaginationContainerResponse<List<NotificationResponse>>

    @GET("notification/subscribe")
    suspend fun getNotificationSubscribes(): List<String>

    @POST("notification/unsubscribe")
    suspend fun setNotificationUnSubscribe(@Body params: JsonObject)

    @POST("notification/subscribe")
    suspend fun setNotificationSubscribe(@Body params: JsonObject)

    @PUT("notification/clear")
    suspend fun clearNotificationCount()
}