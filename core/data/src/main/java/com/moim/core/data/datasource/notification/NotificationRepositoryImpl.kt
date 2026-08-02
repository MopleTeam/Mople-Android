package com.moim.core.data.datasource.notification

import com.moim.core.common.model.Notification
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.data.util.catchFlow
import com.moim.core.remote.datasource.notification.NotificationRemoteDataSource
import com.moim.core.remote.model.NotificationResponse
import com.moim.core.remote.model.asItem
import com.moim.core.remote.util.converterException
import javax.inject.Inject

internal class NotificationRepositoryImpl @Inject constructor(
    private val notificationRemoteDataSource: NotificationRemoteDataSource,
) : NotificationRepository {
    override suspend fun getNotifications(
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Notification>> =
        try {
            notificationRemoteDataSource
                .getNotifications(
                    cursor = cursor,
                    size = size,
                ).asItem {
                    it.map(NotificationResponse::asItem)
                }
        } catch (e: Exception) {
            throw converterException(e)
        }

    override fun getNotificationSubscribes() =
        catchFlow {
            emit(notificationRemoteDataSource.getNotificationSubscribes())
        }

    override fun setNotificationSubscribe(topic: String) =
        catchFlow {
            emit(notificationRemoteDataSource.setNotificationSubscribe(jsonOf(KEY_TOPIC to listOf(topic))))
        }

    override fun setNotificationUnSubscribe(topic: String) =
        catchFlow {
            emit(notificationRemoteDataSource.setNotificationUnSubscribe(jsonOf(KEY_TOPIC to listOf(topic))))
        }

    override fun clearNotificationCount() =
        catchFlow {
            emit(notificationRemoteDataSource.clearNotificationCount())
        }

    companion object {
        private const val KEY_TOPIC = "topics"
    }
}
