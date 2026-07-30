package com.moim.core.common.model

import androidx.compose.runtime.Stable
import com.moim.core.common.model.util.KZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Stable
@Serializable
data class NoticeComment(
    val commentId: String = "",
    val content: String = "",
    val parentId: String? = null,
    val replayCount: Int = 0,
    val writer: Writer,
    @Serializable(with = KZonedDateTimeSerializer::class)
    val commentAt: ZonedDateTime,
    val openGraph: OpenGraph? = null,
)
