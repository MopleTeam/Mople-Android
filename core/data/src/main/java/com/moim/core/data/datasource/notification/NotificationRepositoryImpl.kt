package com.moim.core.data.datasource.notification

import com.moim.core.data.datasource.notification.remote.NotificationDataSource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class NotificationRepositoryImpl @Inject constructor(
    private val remoteDataSource: NotificationDataSource
) : NotificationRepository {

    override fun getNotificationSubscribes() = flow {
        emit(remoteDataSource.getNotificationSubscribes())
    }

    override fun setNotificationSubscribe(topic: String) = flow {
        emit(remoteDataSource.setNotificationSubscribe(topic))
    }

    override fun setNotificationUnSubscribe(topic: String) = flow {
        emit(remoteDataSource.setNotificationUnSubscribe(topic))
    }
}