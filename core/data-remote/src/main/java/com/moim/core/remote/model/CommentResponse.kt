package com.moim.core.remote.model

import com.moim.core.common.model.Comment
import com.moim.core.common.model.Writer
import com.moim.core.common.util.parseZonedDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    @SerialName("postId")
    val postId: String,
    @SerialName("commentId")
    val commentId: String,
    @SerialName("content")
    val content: String,
    @SerialName("parentId")
    val parentId: String? = null,
    @SerialName("replyCount")
    val replayCount: Int? = null,
    @SerialName("likeCount")
    val likeCount: Int,
    @SerialName("likedByMe")
    val isLike: Boolean,
    @SerialName("writer")
    val writer: WriterResponse,
    @SerialName("mentions")
    val mentions: List<WriterResponse>,
    @SerialName("time")
    val commentAt: String,
)

@Serializable
data class WriterResponse(
    @SerialName("userId")
    val userId: String,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("image")
    val image: String? = null,
)

fun CommentResponse.asItem(): Comment {
    return Comment(
        postId = postId,
        commentId = commentId,
        content = content,
        parentId = parentId,
        replayCount = replayCount ?: 0,
        likeCount = likeCount,
        isLike = isLike,
        writer = writer.asItem(),
        mentions = mentions.map(WriterResponse::asItem),
        commentAt = commentAt.parseZonedDateTime(),
    )
}

fun WriterResponse.asItem(): Writer {
    return Writer(
        userId = userId,
        nickname = nickname,
        imageUrl = image ?: ""
    )
}
