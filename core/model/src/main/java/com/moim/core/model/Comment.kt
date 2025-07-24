package com.moim.core.model

import androidx.compose.runtime.Stable
import java.time.ZonedDateTime

@Stable
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
    val commentAt: ZonedDateTime,
)

fun Comment.isChild(): Boolean {
    return parentId != null
}

@Stable
data class Writer(
    val userId: String,
    val nickname: String,
    val imageUrl: String,
)