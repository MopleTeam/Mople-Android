package com.moim.core.data.datasource.notification

import com.moim.core.common.model.Notification
import com.moim.core.common.model.PaginationContainer

interface NotificationRepository {
    suspend fun getNotifications(
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Notification>>

    suspend fun getNotificationSubscribes(): List<String>

    suspend fun setNotificationSubscribe(topic: String)

    suspend fun setNotificationUnSubscribe(topic: String)

    suspend fun clearNotificationCount()
}
