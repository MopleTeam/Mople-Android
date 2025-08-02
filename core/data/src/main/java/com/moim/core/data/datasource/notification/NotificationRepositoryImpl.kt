package com.moim.core.data.datasource.notification

import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.data.mapper.asItem
import com.moim.core.data.util.catchFlow
import com.moim.core.datamodel.NotificationResponse
import com.moim.core.model.Notification
import com.moim.core.model.PaginationContainer
import com.moim.core.network.service.NotificationApi
import com.moim.core.network.util.converterException
import javax.inject.Inject

internal class NotificationRepositoryImpl @Inject constructor(
    private val notificationApi: NotificationApi,
) : NotificationRepository {

    override suspend fun getNotifications(
        cursor: String,
        size: Int
    ): PaginationContainer<List<Notification>> {
        return try {
            notificationApi.getNotifications(
                cursor = cursor,
                size= size
            ).asItem {
                it.map(NotificationResponse::asItem)
            }
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override fun getNotificationSubscribes() = catchFlow {
        emit(notificationApi.getNotificationSubscribes())
    }

    override fun setNotificationSubscribe(topic: String) = catchFlow {
        emit(notificationApi.setNotificationSubscribe(jsonOf(KEY_TOPIC to listOf(topic))))
    }

    override fun setNotificationUnSubscribe(topic: String) = catchFlow {
        emit(notificationApi.setNotificationUnSubscribe(jsonOf(KEY_TOPIC to listOf(topic))))
    }

    override fun clearNotificationCount() = catchFlow {
        emit(notificationApi.clearNotificationCount())
    }

    companion object {
        private const val KEY_TOPIC = "topics"
    }
}