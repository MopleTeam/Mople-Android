package com.moim.core.data.datasource.comment

import com.moim.core.common.model.Comment
import com.moim.core.common.model.PaginationContainer
import kotlinx.coroutines.flow.Flow

interface CommentRepository {

    suspend fun getComments(
        postId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Comment>>

    suspend fun getReplyComments(
        postId: String,
        commentId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Comment>>

    fun createComment(
        postId: String,
        content: String,
        mentionIds: List<String>,
    ): Flow<Comment>

    fun createReplyComment(
        postId: String,
        commentId: String,
        content: String,
        mentionIds: List<String>,
    ): Flow<Comment>

    fun updateComment(
        commentId: String,
        content: String,
        mentionIds: List<String>,
    ): Flow<Comment>

    fun updateLikeComment(commentId: String): Flow<Comment>

    fun deleteComment(commentId: String): Flow<Unit>

    fun reportComment(commentId: String): Flow<Unit>
}
