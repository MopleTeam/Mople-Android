package com.moim.core.data.datasource.notification.remote

internal interface NotificationDataSource {
    suspend fun getNotificationSubscribes(): List<String>

    suspend fun setNotificationSubscribe(topic: String)

    suspend fun setNotificationUnSubscribe(topic: String)
}