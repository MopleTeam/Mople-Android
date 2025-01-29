package com.moim.core.data.datasource.comment.remote

import com.moim.core.datamodel.CommentResponse

internal interface CommentRemoteDataSource {

    suspend fun getComments(postId: String): List<CommentResponse>

    suspend fun createComment(
        postId: String,
        content: String
    ): List<CommentResponse>

    suspend fun updateComment(
        commentId: String,
        content: String,
    ): List<CommentResponse>

    suspend fun deleteComment(commentId: String)

    suspend fun reportComment(commentId: String, reason: String)
}