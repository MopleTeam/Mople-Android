package com.moim.core.data.datasource.comment.remote

import com.moim.core.data.service.CommentApi
import com.moim.core.data.util.JsonUtil.jsonOf
import com.moim.core.data.util.converterException
import com.moim.core.datamodel.CommentResponse
import javax.inject.Inject

internal class CommentRemoteDataSourceImpl @Inject constructor(
    private val commentApi: CommentApi
) : CommentRemoteDataSource {

    override suspend fun getComments(postId: String): List<CommentResponse> {
        return try {
            commentApi.getComments(postId)
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun createComment(postId: String, content: String): List<CommentResponse> {
        return try {
            commentApi.createComment(
                postId = postId,
                params = jsonOf(KEY_CONTENTS to content)
            )
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun updateComment(commentId: String, content: String): List<CommentResponse> {
        return try {
            commentApi.updateComment(
                commentId = commentId,
                params = jsonOf(KEY_CONTENTS to content)
            )
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun deleteComment(commentId: String) {
        return try {
            commentApi.deleteComment(commentId)
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun reportComment(commentId: String) {
        return try {
            commentApi.reportComment(
                params = jsonOf(
                    KEY_COMMENT_ID to commentId,
                    KEY_REASON to ""
                )
            )
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    companion object {
        private const val KEY_COMMENT_ID = "commentId"
        private const val KEY_CONTENTS = "contents"
        private const val KEY_REASON = "reason"
    }
}