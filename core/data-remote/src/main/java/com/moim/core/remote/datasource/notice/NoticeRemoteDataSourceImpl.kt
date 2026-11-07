package com.moim.core.remote.datasource.notice

import com.moim.core.remote.di.qualifiers.MoimApi
import com.moim.core.remote.model.NoticeResponse
import com.moim.core.remote.model.PaginationContainerResponse
import com.moim.core.remote.util.patchJson
import com.moim.core.remote.util.postJson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

internal class NoticeRemoteDataSourceImpl @Inject constructor(
    @MoimApi private val client: HttpClient,
) : NoticeRemoteDataSource {
    override suspend fun getNotices(
        meetId: String,
        cursor: String,
        size: Int,
        type: String?,
    ): PaginationContainerResponse<List<NoticeResponse>> =
        client
            .get("notice/list/$meetId") {
                parameter("cursor", cursor)
                parameter("size", size)
                parameter("type", type)
            }.body()

    override suspend fun getNotice(noticeId: String): NoticeResponse = client.get("/notice/detail/$noticeId").body()

    override suspend fun createNotice(params: JsonObject): NoticeResponse = client.postJson("notice/create", params).body()

    override suspend fun updateNotice(
        noticeId: String,
        params: JsonObject,
    ): NoticeResponse = client.patchJson("notice/update/$noticeId", params).body()

    override suspend fun deleteNotice(noticeId: String) {
        client.delete("notice/$noticeId")
    }

    override suspend fun pinNotice(noticeId: String): NoticeResponse = client.patchJson("notice/pin/$noticeId").body()

    override suspend fun unpinNotice(noticeId: String): NoticeResponse = client.delete("notice/pin/$noticeId").body()
}
