package com.moim.feature.meetingnoticedetail.model

import com.moim.core.common.model.Notice
import com.moim.core.common.model.NoticeType
import java.time.ZonedDateTime

data class NoticeUiModel(
    val noticeId: String,
    val type: NoticeType,
    val writerNickname: String,
    val writerImageUrl: String,
    val content: String,
    val createdAt: ZonedDateTime,
)

fun Notice.asUiModel(): NoticeUiModel =
    NoticeUiModel(
        noticeId = noticeId,
        type = type,
        writerNickname = writer?.nickname.orEmpty(),
        writerImageUrl = writer?.imageUrl.orEmpty(),
        content = content,
        createdAt = createdAt,
    )
