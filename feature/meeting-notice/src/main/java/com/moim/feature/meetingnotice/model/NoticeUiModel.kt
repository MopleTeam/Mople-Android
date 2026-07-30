package com.moim.feature.meetingnotice.model

import com.moim.core.common.model.Notice
import com.moim.core.common.model.NoticeType
import java.time.ZonedDateTime

data class NoticeUiModel(
    val noticeId: String,
    val meetId: String,
    val type: NoticeType,
    val content: String,
    val createdAt: ZonedDateTime,
    val pinned: Boolean,
)

fun Notice.asUiModel(): NoticeUiModel =
    NoticeUiModel(
        noticeId = noticeId,
        meetId = meetId,
        type = type,
        content = content,
        createdAt = createdAt,
        pinned = pinned,
    )
