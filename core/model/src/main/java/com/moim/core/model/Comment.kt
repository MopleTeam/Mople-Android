package com.moim.core.model

import androidx.compose.runtime.Stable
import com.moim.core.datamodel.CommentResponse

@Stable
data class Comment(
    val postId: String = "",
    val commentId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userImageUrl: String = "",
    val content: String = "",
    val commentAt: String = "",
    val isUpdate: Boolean = false
)

fun CommentResponse.asItem(): Comment {
    return Comment(
        postId = postId,
        commentId = commentId,
        userId = userId,
        userName = userName,
        userImageUrl = userImageUrl ?: "",
        content = content,
        commentAt = commentAt,
        isUpdate = isUpdate
    )
}