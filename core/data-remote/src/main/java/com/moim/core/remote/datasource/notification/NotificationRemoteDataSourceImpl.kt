package com.moim.core.remote.datasource.notification

import com.moim.core.remote.di.qualifiers.MoimApi
import com.moim.core.remote.model.NotificationResponse
import com.moim.core.remote.model.PaginationContainerResponse
import com.moim.core.remote.util.postJson
import com.moim.core.remote.util.putJson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

internal class NotificationRemoteDataSourceImpl @Inject constructor(
    @MoimApi private val client: HttpClient,
) : NotificationRemoteDataSource {
    override suspend fun getNotifications(
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<NotificationResponse>> =
        client
            .get("notification/list") {
                parameter("cursor", cursor)
                parameter("size", size)
            }.body()

    override suspend fun getNotificationSubscribes(): List<String> = client.get("notification/subscribe").body()

    override suspend fun setNotificationUnSubscribe(params: JsonObject) {
        client.postJson("notification/unsubscribe", params)
    }

    override suspend fun setNotificationSubscribe(params: JsonObject) {
        client.postJson("notification/subscribe", params)
    }

    override suspend fun clearNotificationCount() {
        client.putJson("notification/clear")
    }
}
