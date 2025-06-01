package com.moim.core.data.datasource.notification

import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.data.util.catchFlow
import com.moim.core.datamodel.NotificationResponse
import com.moim.core.model.asItem
import com.moim.core.network.service.NotificationApi
import javax.inject.Inject

internal class NotificationRepositoryImpl @Inject constructor(
    private val notificationApi: NotificationApi,
) : NotificationRepository {

    override fun getNotifications() = catchFlow {
        emit(notificationApi.getNotifications().map(NotificationResponse::asItem))
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