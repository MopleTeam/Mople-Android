package com.moim.core.data.datasource.comment

import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.data.mapper.asItem
import com.moim.core.data.util.catchFlow
import com.moim.core.datamodel.CommentResponse
import com.moim.core.model.Comment
import com.moim.core.model.PaginationContainer
import com.moim.core.network.service.CommentApi
import com.moim.core.network.util.converterException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class CommentRepositoryImpl @Inject constructor(
    private val commentApi: CommentApi,
) : CommentRepository {

    override suspend fun getComments(
        postId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Comment>> {
        return try {
            commentApi
                .getComments(
                    postId = postId,
                    cursor = cursor,
                    size = size
                )
                .asItem { it.map(CommentResponse::asItem) }
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override fun createComment(postId: String, content: String): Flow<Comment> = catchFlow {
        emit(
            commentApi.createComment(
                postId = postId,
                params = jsonOf(KEY_CONTENTS to content)
            ).asItem()
        )
    }

    override fun updateComment(commentId: String, content: String): Flow<Comment> = catchFlow {
        emit(
            commentApi.updateComment(
                commentId = commentId,
                params = jsonOf(
                    KEY_CONTENTS to content,
                    KEY_MENTIONS to listOf<String>()
                )
            ).asItem()
        )
    }

    override fun updateLikeComment(commentId: String): Flow<Comment> = catchFlow {
        emit(commentApi.updateLikeComment(commentId).asItem())
    }

    override fun deleteComment(commentId: String): Flow<Unit> = catchFlow {
        emit(commentApi.deleteComment(commentId))
    }

    override fun reportComment(commentId: String): Flow<Unit> = catchFlow {
        emit(commentApi.reportComment(jsonOf(KEY_COMMENT_ID to commentId, KEY_REASON to "")))
    }

    companion object {
        private const val KEY_COMMENT_ID = "commentId"
        private const val KEY_CONTENTS = "contents"
        private const val KEY_MENTIONS = "mentions"
        private const val KEY_REASON = "reason"
    }
}