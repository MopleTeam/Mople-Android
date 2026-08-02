package com.moim.core.data.datasource.notification

import com.moim.core.common.model.Notification
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.remote.datasource.notification.NotificationRemoteDataSource
import com.moim.core.remote.model.NotificationResponse
import com.moim.core.remote.model.asItem
import kotlinx.coroutines.flow.flow
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

    override fun getNotificationSubscribes() =
        flow {
            emit(notificationRemoteDataSource.getNotificationSubscribes())
        }

    override fun setNotificationSubscribe(topic: String) =
        flow {
            emit(notificationRemoteDataSource.setNotificationSubscribe(jsonOf(KEY_TOPIC to listOf(topic))))
        }

    override fun setNotificationUnSubscribe(topic: String) =
        flow {
            emit(notificationRemoteDataSource.setNotificationUnSubscribe(jsonOf(KEY_TOPIC to listOf(topic))))
        }

    override fun clearNotificationCount() =
        flow {
            emit(notificationRemoteDataSource.clearNotificationCount())
        }

    companion object {
        private const val KEY_TOPIC = "topics"
    }
}
