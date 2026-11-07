package com.moim.core.data.datasource.notification

import com.moim.core.common.model.Notification
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.remote.datasource.notification.NotificationRemoteDataSource
import com.moim.core.remote.model.NotificationResponse
import com.moim.core.remote.model.asItem
import javax.inject.Inject

internal class NotificationRepositoryImpl @Inject constructor(
    private val notificationRemoteDataSource: NotificationRemoteDataSource,
) : NotificationRepository {
    override suspend fun getNotifications(
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Notification>> =
        notificationRemoteDataSource
            .getNotifications(
                cursor = cursor,
                size = size,
            ).asItem {
                it.map(NotificationResponse::asItem)
            }

    override suspend fun getNotificationSubscribes(): List<String> = notificationRemoteDataSource.getNotificationSubscribes()

    override suspend fun setNotificationSubscribe(topic: String) {
        notificationRemoteDataSource.setNotificationSubscribe(jsonOf(KEY_TOPIC to listOf(topic)))
    }

    override suspend fun setNotificationUnSubscribe(topic: String) {
        notificationRemoteDataSource.setNotificationUnSubscribe(jsonOf(KEY_TOPIC to listOf(topic)))
    }

    override suspend fun clearNotificationCount() {
        notificationRemoteDataSource.clearNotificationCount()
    }

    companion object {
        private const val KEY_TOPIC = "topics"
    }
}
