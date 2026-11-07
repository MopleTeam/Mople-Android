package com.moim.core.data.datasource.comment

import com.moim.core.common.model.Comment
import com.moim.core.common.model.NoticeComment
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.remote.datasource.comment.CommentRemoteDataSource
import com.moim.core.remote.datasource.opengraph.OpenGraphRemoteDataSource
import com.moim.core.remote.model.asItem
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

internal class CommentRepositoryImpl @Inject constructor(
    private val commentRemoteDataSource: CommentRemoteDataSource,
    private val openGraphRemoteDataSource: OpenGraphRemoteDataSource,
) : CommentRepository {
    override suspend fun getComments(
        postId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Comment>> =
        coroutineScope {
            val commentContainer =
                commentRemoteDataSource.getComments(
                    postId = postId,
                    cursor = cursor,
                    size = size,
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
        }

    override suspend fun getNoticeComments(
        noticeId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<NoticeComment>> =
        coroutineScope {
            val commentContainer =
                commentRemoteDataSource.getNoticeComments(
                    postId = noticeId,
                    cursor = cursor,
                    size = size,
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
        }

    override suspend fun getReplyComments(
        postId: String,
        commentId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Comment>> =
        coroutineScope {
            val commentContainer =
                commentRemoteDataSource
                    .getReplyComments(
                        postId = postId,
                        commentId = commentId,
                        cursor = cursor,
                        size = size,
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
        }

    override suspend fun createComment(
        postId: String,
        content: String,
        mentionIds: List<String>,
    ): Comment {
        val comment =
            commentRemoteDataSource
                .createComment(
                    postId = postId,
                    params =
                        jsonOf(
                            KEY_CONTENTS to content,
                            KEY_MENTIONS to mentionIds,
                        ),
                )
        val openGraph =
            openGraphRemoteDataSource
                .getOpenGraph(url = comment.content.findWebLink())

        return comment.asItem(openGraph)
    }

    override suspend fun createReplyComment(
        postId: String,
        commentId: String,
        content: String,
        mentionIds: List<String>,
    ): Comment {
        val comment =
            commentRemoteDataSource
                .createReplyComment(
                    postId = postId,
                    commentId = commentId,
                    params =
                        jsonOf(
                            KEY_CONTENTS to content,
                            KEY_MENTIONS to mentionIds,
                        ),
                )
        val openGraph =
            openGraphRemoteDataSource.getOpenGraph(url = comment.content.findWebLink())

        return comment.asItem(openGraph)
    }

    override suspend fun updateComment(
        commentId: String,
        content: String,
        mentionIds: List<String>,
    ): Comment {
        val comment =
            commentRemoteDataSource
                .updateComment(
                    commentId = commentId,
                    params =
                        jsonOf(
                            KEY_CONTENTS to content,
                            KEY_MENTIONS to mentionIds,
                        ),
                )
        val openGraph =
            openGraphRemoteDataSource
                .getOpenGraph(url = comment.content.findWebLink())

        return comment.asItem(openGraph)
    }

    override suspend fun updateLikeComment(commentId: String): Comment {
        val comment = commentRemoteDataSource.updateLikeComment(commentId)
        val openGraph = openGraphRemoteDataSource.getOpenGraph(url = comment.content.findWebLink())

        return comment.asItem(openGraph)
    }

    override suspend fun deleteComment(commentId: String) {
        commentRemoteDataSource.deleteComment(commentId)
    }

    override suspend fun createNoticeComment(
        noticeId: String,
        content: String,
    ): NoticeComment {
        val comment =
            commentRemoteDataSource
                .createNoticeComment(
                    postId = noticeId,
                    params = jsonOf(KEY_CONTENTS to content),
                )
        val openGraph =
            openGraphRemoteDataSource
                .getOpenGraph(url = comment.content.findWebLink())

        return comment.asItem(openGraph)
    }

    override suspend fun updateNoticeComment(
        commentId: String,
        content: String,
    ): NoticeComment {
        val comment =
            commentRemoteDataSource
                .updateNoticeComment(
                    commentId = commentId,
                    params = jsonOf(KEY_CONTENTS to content),
                )
        val openGraph =
            openGraphRemoteDataSource
                .getOpenGraph(url = comment.content.findWebLink())

        return comment.asItem(openGraph)
    }

    override suspend fun reportComment(commentId: String) {
        commentRemoteDataSource.reportComment(jsonOf(KEY_COMMENT_ID to commentId, KEY_REASON to ""))
    }

    private fun String.findWebLink(): String? {
        val urlPattern =
            Regex(
                pattern = """(https?://\S+)|(www\.\S+)|([a-zA-Z0-9-]+\.[a-zA-Z]{2,}\S*)""",
                option = RegexOption.IGNORE_CASE,
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
