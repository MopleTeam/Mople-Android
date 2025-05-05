package com.moim.core.data.service

import com.moim.core.datamodel.NotificationResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

internal interface NotificationApi {
    @GET("/notification/list")
    suspend fun getNotifications(): List<NotificationResponse>

    @GET("/notification/subscribe")
    suspend fun getNotificationSubscribes(): List<String>

    @POST("/notification/unsubscribe")
    suspend fun setNotificationUnSubscribe(@Body params: JsonObject)

    @POST("/notification/subscribe")
    suspend fun setNotificationSubscribe(@Body params: JsonObject)
}