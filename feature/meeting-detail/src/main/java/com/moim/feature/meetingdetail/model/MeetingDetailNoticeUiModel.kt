package com.moim.feature.meetingdetail.model

import com.moim.core.common.model.Notice
import com.moim.core.common.model.NoticeType

data class MeetingDetailNoticeUiModel(
    val noticeId: String,
    val content: String,
    val noticeType: NoticeType,
)

fun Notice.toUiModel(): MeetingDetailNoticeUiModel =
    MeetingDetailNoticeUiModel(
        noticeId = noticeId,
        content = content,
        noticeType = type,
    )
