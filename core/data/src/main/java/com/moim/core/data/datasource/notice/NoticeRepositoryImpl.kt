package com.moim.core.data.datasource.notice

import com.moim.core.common.model.Notice
import com.moim.core.common.model.NoticeType
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.remote.datasource.notice.NoticeRemoteDataSource
import com.moim.core.remote.model.NoticeResponse
import com.moim.core.remote.model.asItem
import javax.inject.Inject

internal class NoticeRepositoryImpl @Inject constructor(
    private val noticeRemoteDataSource: NoticeRemoteDataSource,
) : NoticeRepository {
    override suspend fun getNotices(
        meetId: String,
        cursor: String,
        size: Int,
        filterType: NoticeType?,
    ): PaginationContainer<List<Notice>> =
        noticeRemoteDataSource
            .getNotices(
                meetId = meetId,
                cursor = cursor,
                size = size,
                type = filterType?.name,
            ).asItem { it.map(NoticeResponse::asItem) }

    override suspend fun getNotice(noticeId: String): Notice = noticeRemoteDataSource.getNotice(noticeId).asItem()

    override suspend fun createNotice(
        meetId: String,
        content: String,
    ): Notice =
        noticeRemoteDataSource
            .createNotice(
                params =
                    jsonOf(
                        KEY_MEET_ID to meetId,
                        KEY_CONTENT to content,
                    ),
            ).asItem()

    override suspend fun updateNotice(
        noticeId: String,
        meetId: String,
        content: String,
    ): Notice =
        noticeRemoteDataSource
            .updateNotice(
                noticeId = noticeId,
                params =
                    jsonOf(
                        KEY_MEET_ID to meetId,
                        KEY_CONTENT to content,
                    ),
            ).asItem()

    override suspend fun deleteNotice(noticeId: String) {
        noticeRemoteDataSource.deleteNotice(noticeId = noticeId)
    }

    override suspend fun pinNotice(noticeId: String): Notice = noticeRemoteDataSource.pinNotice(noticeId = noticeId).asItem()

    override suspend fun unpinNotice(noticeId: String): Notice = noticeRemoteDataSource.unpinNotice(noticeId = noticeId).asItem()

    companion object {
        private const val KEY_MEET_ID = "meetId"
        private const val KEY_CONTENT = "content"
    }
}
