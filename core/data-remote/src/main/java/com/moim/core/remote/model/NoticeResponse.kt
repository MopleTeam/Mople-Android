package com.moim.core.remote.model

import com.moim.core.common.model.Notice
import com.moim.core.common.model.NoticeType
import com.moim.core.common.util.parseZonedDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoticeResponse(
    @SerialName("noticeId")
    val noticeId: Long,
    @SerialName("version")
    val version: Int,
    @SerialName("meetId")
    val meetId: Long,
    @SerialName("type")
    val type: String,
    @SerialName("content")
    val content: String,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("pinned")
    val pinned: Boolean,
)

fun NoticeResponse.asItem(): Notice =
    Notice(
        noticeId = noticeId.toString(),
        version = version,
        meetId = meetId.toString(),
        type = NoticeType.entries.find { it.name == type } ?: NoticeType.NONE,
        content = content,
        createdAt = createdAt.parseZonedDateTime(),
        pinned = pinned,
    )
