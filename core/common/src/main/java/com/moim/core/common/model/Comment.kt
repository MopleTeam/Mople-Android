package com.moim.core.common.model

import androidx.compose.runtime.Stable
import com.moim.core.common.model.util.KZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Stable
@Serializable
data class Comment(
    val postId: String = "",
    val commentId: String = "",
    val content: String = "",
    val parentId: String? = null,
    val replayCount: Int = 0,
    val likeCount: Int = 0,
    val isLike: Boolean = false,
    val writer: Writer,
    val mentions: List<Writer> = emptyList(),
    @Serializable(with = KZonedDateTimeSerializer::class)
    val commentAt: ZonedDateTime,
    val openGraph: OpenGraph? = null,
)

fun Comment.isChild(): Boolean {
    return parentId != null
}

@Stable
@Serializable
data class Writer(
    val userId: String,
    val nickname: String,
    val imageUrl: String,
)
