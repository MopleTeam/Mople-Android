package com.moim.core.datamodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    @SerialName("postId")
    val postId: String,
    @SerialName("commentId")
    val commentId: String,
    @SerialName("writerId")
    val userId: String,
    @SerialName("writerName")
    val userName: String,
    @SerialName("writerImage")
    val userImageUrl: String? = null,
    @SerialName("content")
    val content: String,
    @SerialName("time")
    val commentAt: String,
    @SerialName("update")
    val isUpdate: Boolean = false
)