package com.moim.core.remote.datasource.comment

import com.moim.core.remote.model.CommentResponse
import com.moim.core.remote.model.NoticeCommentResponse
import com.moim.core.remote.model.PaginationContainerResponse
import kotlinx.serialization.json.JsonObject

interface CommentRemoteDataSource {
    suspend fun getComments(
        postId: String,
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<CommentResponse>>

    suspend fun getReplyComments(
        postId: String,
        commentId: String,
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<CommentResponse>>

    suspend fun getNoticeComments(
        postId: String,
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<NoticeCommentResponse>>

    suspend fun createComment(
        postId: String,
        params: JsonObject,
    ): CommentResponse

    suspend fun createReplyComment(
        postId: String,
        commentId: String,
        params: JsonObject,
    ): CommentResponse

    suspend fun createNoticeComment(
        postId: String,
        params: JsonObject,
    ): NoticeCommentResponse

    suspend fun updateNoticeComment(
        commentId: String,
        params: JsonObject,
    ): NoticeCommentResponse

    suspend fun updateLikeComment(commentId: String): CommentResponse

    // Common
    suspend fun updateComment(
        commentId: String,
        params: JsonObject,
    ): CommentResponse

    suspend fun reportComment(params: JsonObject)

    suspend fun deleteComment(commentId: String)
}
