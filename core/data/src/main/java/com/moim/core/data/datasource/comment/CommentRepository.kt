package com.moim.core.data.datasource.comment

import com.moim.core.common.model.Comment
import com.moim.core.common.model.NoticeComment
import com.moim.core.common.model.PaginationContainer

interface CommentRepository {
    suspend fun getComments(
        postId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Comment>>

    suspend fun getNoticeComments(
        noticeId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<NoticeComment>>

    suspend fun getReplyComments(
        postId: String,
        commentId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Comment>>

    suspend fun createComment(
        postId: String,
        content: String,
        mentionIds: List<String>,
    ): Comment

    suspend fun createReplyComment(
        postId: String,
        commentId: String,
        content: String,
        mentionIds: List<String>,
    ): Comment

    suspend fun updateComment(
        commentId: String,
        content: String,
        mentionIds: List<String>,
    ): Comment

    suspend fun updateLikeComment(commentId: String): Comment

    suspend fun deleteComment(commentId: String)

    suspend fun createNoticeComment(
        noticeId: String,
        content: String,
    ): NoticeComment

    suspend fun updateNoticeComment(
        commentId: String,
        content: String,
    ): NoticeComment

    suspend fun reportComment(commentId: String)
}
