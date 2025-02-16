package com.moim.core.data.datasource.notification

import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    fun getNotificationSubscribes(): Flow<List<String>>

    fun setNotificationSubscribe(topic: String): Flow<Unit>

    fun setNotificationUnSubscribe(topic: String): Flow<Unit>
}