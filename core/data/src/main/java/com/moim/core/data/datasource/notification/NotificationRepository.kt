package com.moim.core.data.datasource.notification

import com.moim.core.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    fun getNotifications() : Flow<List<Notification>>

    fun getNotificationSubscribes(): Flow<List<String>>

    fun setNotificationSubscribe(topic: String): Flow<Unit>

    fun setNotificationUnSubscribe(topic: String): Flow<Unit>

    fun clearNotificationCount() : Flow<Unit>
}