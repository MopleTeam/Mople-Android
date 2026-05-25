package com.moim.core.data.datasource.notice

import com.moim.core.common.model.Notice
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.data.util.catchFlow
import com.moim.core.remote.model.NoticeResponse
import com.moim.core.remote.model.asItem
import com.moim.core.remote.service.NoticeApi
import com.moim.core.remote.util.converterException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class NoticeRepositoryImpl @Inject constructor(
    private val noticeApi: NoticeApi,
) : NoticeRepository {
    override suspend fun getNotices(
        meetId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Notice>> =
        try {
            noticeApi
                .getNotices(
                    meetId = meetId,
                    cursor = cursor,
                    size = size,
                ).asItem { it.map(NoticeResponse::asItem) }
        } catch (e: Exception) {
            throw converterException(e)
        }

    override fun createNotice(
        meetId: String,
        content: String,
    ): Flow<Notice> =
        catchFlow {
            emit(
                noticeApi
                    .createNotice(
                        params =
                            jsonOf(
                                KEY_MEET_ID to meetId,
                                KEY_CONTENT to content,
                            ),
                    ).asItem(),
            )
        }

    override fun updateNotice(
        noticeId: String,
        meetId: String,
        content: String,
    ): Flow<Notice> =
        catchFlow {
            emit(
                noticeApi
                    .updateNotice(
                        noticeId = noticeId,
                        params =
                            jsonOf(
                                KEY_MEET_ID to meetId,
                                KEY_CONTENT to content,
                            ),
                    ).asItem(),
            )
        }

    override fun deleteNotice(noticeId: String): Flow<Unit> =
        catchFlow {
            emit(noticeApi.deleteNotice(noticeId = noticeId))
        }

    override fun pinNotice(noticeId: String): Flow<Notice> =
        catchFlow {
            emit(noticeApi.pinNotice(noticeId = noticeId).asItem())
        }

    override fun unpinNotice(noticeId: String): Flow<Notice> =
        catchFlow {
            emit(noticeApi.unpinNotice(noticeId = noticeId).asItem())
        }

    companion object {
        private const val KEY_MEET_ID = "meetId"
        private const val KEY_CONTENT = "content"
    }
}
