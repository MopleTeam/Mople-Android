package com.moim.core.datamodel

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
    val image: String,
)