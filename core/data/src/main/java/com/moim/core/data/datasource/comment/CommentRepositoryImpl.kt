package com.moim.core.data.datasource.comment

import com.moim.core.common.model.Comment
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.data.util.catchFlow
import com.moim.core.remote.datasource.opengraph.OpenGraphRemoteDataSource
import com.moim.core.remote.model.asItem
import com.moim.core.remote.service.CommentApi
import com.moim.core.remote.util.converterException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class CommentRepositoryImpl @Inject constructor(
    private val commentApi: CommentApi,
    private val openGraphRemoteDataSource: OpenGraphRemoteDataSource,
) : CommentRepository {

    override suspend fun getComments(
        postId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Comment>> = coroutineScope {
        try {
            val commentContainer =
                commentApi.getComments(
                    postId = postId,
                    cursor = cursor,
                    size = size
                )
            val commentItems =
                commentContainer
                    .content
                    .map { comment ->
                        async {
                            val openGraph = openGraphRemoteDataSource.getOpenGraph(comment.content.findWebLink())
                            comment.asItem(openGraph)
                        }
                    }.awaitAll()

            commentContainer.asItem { commentItems }
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun getReplyComments(
        postId: String,
        commentId: String,
        cursor: String,
        size: Int
    ): PaginationContainer<List<Comment>> = coroutineScope {
        try {
            val commentContainer =
                commentApi
                    .getReplyComments(
                        postId = postId,
                        commentId = commentId,
                        cursor = cursor,
                        size = size
                    )
            val commentItems =
                commentContainer
                    .content
                    .map { comment ->
                        async {
                            val openGraph = openGraphRemoteDataSource.getOpenGraph(comment.content.findWebLink())
                            comment.asItem(openGraph)
                        }
                    }.awaitAll()

            commentContainer.asItem { commentItems }
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override fun createComment(
        postId: String,
        content: String,
        mentionIds: List<String>,
    ): Flow<Comment> = catchFlow {
        val comment =
            commentApi
                .createComment(
                    postId = postId,
                    params =
                        jsonOf(
                            KEY_CONTENTS to content,
                            KEY_MENTIONS to mentionIds,
                        )
                )
        val openGraph =
            openGraphRemoteDataSource
                .getOpenGraph(url = comment.content.findWebLink())

        emit(comment.asItem(openGraph))
    }

    override fun createReplyComment(
        postId: String,
        commentId: String,
        content: String,
        mentionIds: List<String>
    ): Flow<Comment> = catchFlow {
        val comment =
            commentApi
                .createReplyComment(
                    postId = postId,
                    commentId = commentId,
                    params = jsonOf(
                        KEY_CONTENTS to content,
                        KEY_MENTIONS to mentionIds,
                    )
                )
        val openGraph =
            openGraphRemoteDataSource.getOpenGraph(url = comment.content.findWebLink())

        emit(comment.asItem(openGraph))
    }

    override fun updateComment(
        commentId: String,
        content: String,
        mentionIds: List<String>,
    ): Flow<Comment> = catchFlow {
        val comment =
            commentApi
                .updateComment(
                    commentId = commentId,
                    params = jsonOf(
                        KEY_CONTENTS to content,
                        KEY_MENTIONS to mentionIds,
                    )
                )
        val openGraph =
            openGraphRemoteDataSource
                .getOpenGraph(url = comment.content.findWebLink())

        emit(comment.asItem(openGraph))
    }

    override fun updateLikeComment(commentId: String): Flow<Comment> = catchFlow {
        val comment = commentApi.updateLikeComment(commentId)
        val openGraph = openGraphRemoteDataSource.getOpenGraph(url = comment.content.findWebLink())
        emit(comment.asItem(openGraph))
    }

    override fun deleteComment(commentId: String): Flow<Unit> = catchFlow {
        emit(commentApi.deleteComment(commentId))
    }

    override fun reportComment(commentId: String): Flow<Unit> = catchFlow {
        emit(commentApi.reportComment(jsonOf(KEY_COMMENT_ID to commentId, KEY_REASON to "")))
    }

    private fun String.findWebLink(): String? {
        val urlPattern =
            Regex(
                pattern = """(https?://\S+)|(www\.\S+)|([a-zA-Z0-9-]+\.[a-zA-Z]{2,}\S*)""",
                option = RegexOption.IGNORE_CASE
            )

        return urlPattern.find(this)?.value
    }

    companion object {
        private const val KEY_COMMENT_ID = "commentId"
        private const val KEY_CONTENTS = "contents"
        private const val KEY_MENTIONS = "mentions"
        private const val KEY_REASON = "reason"
    }
}