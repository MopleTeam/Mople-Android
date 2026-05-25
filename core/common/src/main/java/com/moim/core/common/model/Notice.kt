package com.moim.core.common.model

import java.time.ZonedDateTime

enum class NoticeType {
    CUSTOM,
    SYSTEM,
    NONE,
}

data class Notice(
    val noticeId: String,
    val version: Int,
    val meetId: String,
    val type: NoticeType,
    val content: String,
    val createdAt: ZonedDateTime,
    val pinned: Boolean,
)
