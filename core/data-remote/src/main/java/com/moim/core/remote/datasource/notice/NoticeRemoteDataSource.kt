package com.moim.core.remote.datasource.notice

import com.moim.core.remote.model.NoticeResponse
import com.moim.core.remote.model.PaginationContainerResponse
import kotlinx.serialization.json.JsonObject

interface NoticeRemoteDataSource {
    suspend fun getNotices(
        meetId: String,
        cursor: String,
        size: Int,
        type: String?,
    ): PaginationContainerResponse<List<NoticeResponse>>

    suspend fun getNotice(noticeId: String): NoticeResponse

    suspend fun createNotice(params: JsonObject): NoticeResponse

    suspend fun updateNotice(
        noticeId: String,
        params: JsonObject,
    ): NoticeResponse

    suspend fun deleteNotice(noticeId: String)

    suspend fun pinNotice(noticeId: String): NoticeResponse

    suspend fun unpinNotice(noticeId: String): NoticeResponse
}
