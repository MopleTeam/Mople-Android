package com.moim.core.data.datasource.notice

import com.moim.core.common.model.Notice
import com.moim.core.common.model.NoticeType
import com.moim.core.common.model.PaginationContainer
import kotlinx.coroutines.flow.Flow

interface NoticeRepository {
    suspend fun getNotices(
        meetId: String,
        cursor: String,
        size: Int,
        filterType: NoticeType?,
    ): PaginationContainer<List<Notice>>

    fun createNotice(
        meetId: String,
        content: String,
    ): Flow<Notice>

    fun updateNotice(
        noticeId: String,
        meetId: String,
        content: String,
    ): Flow<Notice>

    fun deleteNotice(noticeId: String): Flow<Unit>

    fun pinNotice(noticeId: String): Flow<Notice>

    fun unpinNotice(noticeId: String): Flow<Notice>
}
