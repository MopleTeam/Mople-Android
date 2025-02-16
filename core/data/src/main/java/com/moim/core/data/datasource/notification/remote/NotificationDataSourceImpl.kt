package com.moim.core.data.datasource.notification.remote

import com.moim.core.data.service.NotificationApi
import com.moim.core.data.util.JsonUtil.jsonOf
import com.moim.core.data.util.converterException
import javax.inject.Inject

internal class NotificationDataSourceImpl @Inject constructor(
    private val notificationApi: NotificationApi
) : NotificationDataSource {
    override suspend fun getNotificationSubscribes(): List<String> {
        return try {
            notificationApi.getNotificationSubscribes()
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun setNotificationSubscribe(topic: String) {
        return try {
            notificationApi.setNotificationSubscribe(jsonOf(KEY_TOPIC to listOf(topic)))
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun setNotificationUnSubscribe(topic: String) {
        return try {
            notificationApi.setNotificationUnSubscribe(jsonOf(KEY_TOPIC to listOf(topic)))
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    companion object {
        private const val KEY_TOPIC = "topic"
    }
}