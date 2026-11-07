package com.moim.core.data.datasource.notice

import com.moim.core.common.model.Notice
import com.moim.core.common.model.NoticeType
import com.moim.core.common.model.PaginationContainer

interface NoticeRepository {
    suspend fun getNotices(
        meetId: String,
        cursor: String,
        size: Int,
        filterType: NoticeType?,
    ): PaginationContainer<List<Notice>>

    suspend fun getNotice(noticeId: String): Notice

    suspend fun createNotice(
        meetId: String,
        content: String,
    ): Notice

    suspend fun updateNotice(
        noticeId: String,
        meetId: String,
        content: String,
    ): Notice

    suspend fun deleteNotice(noticeId: String)

    suspend fun pinNotice(noticeId: String): Notice

    suspend fun unpinNotice(noticeId: String): Notice
}
