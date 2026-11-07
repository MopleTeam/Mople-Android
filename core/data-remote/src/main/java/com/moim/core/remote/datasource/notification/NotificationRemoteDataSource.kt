package com.moim.core.remote.datasource.notification

import com.moim.core.remote.model.NotificationResponse
import com.moim.core.remote.model.PaginationContainerResponse
import kotlinx.serialization.json.JsonObject

interface NotificationRemoteDataSource {
    suspend fun getNotifications(
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<NotificationResponse>>

    suspend fun getNotificationSubscribes(): List<String>

    suspend fun setNotificationUnSubscribe(params: JsonObject)

    suspend fun setNotificationSubscribe(params: JsonObject)

    suspend fun clearNotificationCount()
}
