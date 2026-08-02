package com.moim.core.remote.datasource.comment

import com.moim.core.remote.di.qualifiers.MoimApi
import com.moim.core.remote.model.CommentResponse
import com.moim.core.remote.model.NoticeCommentResponse
import com.moim.core.remote.model.PaginationContainerResponse
import com.moim.core.remote.util.patchJson
import com.moim.core.remote.util.postJson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

internal class CommentRemoteDataSourceImpl @Inject constructor(
    @MoimApi private val client: HttpClient,
) : CommentRemoteDataSource {
    override suspend fun getComments(
        postId: String,
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<CommentResponse>> =
        client
            .get("comment/post/$postId") {
                parameter("cursor", cursor)
                parameter("size", size)
            }.body()

    override suspend fun getReplyComments(
        postId: String,
        commentId: String,
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<CommentResponse>> =
        client
            .get("comment/post/$postId/$commentId") {
                parameter("cursor", cursor)
                parameter("size", size)
            }.body()

    override suspend fun getNoticeComments(
        postId: String,
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<NoticeCommentResponse>> =
        client
            .get("comment/notice/$postId") {
                parameter("cursor", cursor)
                parameter("size", size)
            }.body()

    override suspend fun createComment(
        postId: String,
        params: JsonObject,
    ): CommentResponse = client.postJson("comment/post/$postId", params).body()

    override suspend fun createReplyComment(
        postId: String,
        commentId: String,
        params: JsonObject,
    ): CommentResponse = client.postJson("comment/post/$postId/$commentId", params).body()

    override suspend fun createNoticeComment(
        postId: String,
        params: JsonObject,
    ): NoticeCommentResponse = client.postJson("comment/notice/$postId", params).body()

    override suspend fun updateNoticeComment(
        commentId: String,
        params: JsonObject,
    ): NoticeCommentResponse = client.patchJson("comment/notice/$commentId", params).body()

    override suspend fun updateLikeComment(commentId: String): CommentResponse = client.postJson("comment/post/$commentId/likes").body()

    // Common
    override suspend fun updateComment(
        commentId: String,
        params: JsonObject,
    ): CommentResponse = client.patchJson("comment/$commentId", params).body()

    override suspend fun reportComment(params: JsonObject) {
        client.postJson("comment/report", params)
    }

    override suspend fun deleteComment(commentId: String) {
        client.delete("comment/$commentId")
    }
}
