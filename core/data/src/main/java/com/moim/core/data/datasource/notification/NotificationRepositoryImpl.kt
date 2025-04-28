package com.moim.core.data.datasource.notification

import com.moim.core.data.service.NotificationApi
import com.moim.core.data.util.JsonUtil.jsonOf
import com.moim.core.data.util.catchFlow
import javax.inject.Inject

internal class NotificationRepositoryImpl @Inject constructor(
    private val notificationApi: NotificationApi,
) : NotificationRepository {

    override fun getNotificationSubscribes() = catchFlow {
        emit(notificationApi.getNotificationSubscribes())
    }

    override fun setNotificationSubscribe(topic: String) = catchFlow {
        emit(notificationApi.setNotificationSubscribe(jsonOf(KEY_TOPIC to listOf(topic))))
    }

    override fun setNotificationUnSubscribe(topic: String) = catchFlow {
        emit(notificationApi.setNotificationUnSubscribe(jsonOf(KEY_TOPIC to listOf(topic))))
    }

    companion object {
        private const val KEY_TOPIC = "topics"
    }
}