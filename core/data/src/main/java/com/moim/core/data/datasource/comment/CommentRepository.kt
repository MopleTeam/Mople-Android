package com.moim.core.data.datasource.comment

import com.moim.core.datamodel.CommentResponse
import kotlinx.coroutines.flow.Flow

interface CommentRepository {

    fun getComments(postId: String): Flow<List<CommentResponse>>

    fun createComment(
        postId: String,
        content: String
    ): Flow<List<CommentResponse>>

    fun updateComment(
        commentId: String,
        content: String,
    ): Flow<List<CommentResponse>>

    fun deleteComment(commentId: String): Flow<Unit>

    fun reportComment(commentId: String, reason: String): Flow<Unit>
}