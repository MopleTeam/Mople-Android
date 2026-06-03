package com.moim.core.remote.service

import com.moim.core.remote.model.CommentResponse
import com.moim.core.remote.model.PaginationContainerResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CommentApi {
    @GET("comment/post/{postId}")
    suspend fun getComments(
        @Path("postId") postId: String,
        @Query("cursor") cursor: String,
        @Query("size") size: Int,
    ): PaginationContainerResponse<List<CommentResponse>>

    @GET("comment/post/{postId}/{commentId}")
    suspend fun getReplyComments(
        @Path("postId") postId: String,
        @Path("commentId") commentId: String,
        @Query("cursor") cursor: String,
        @Query("size") size: Int,
    ): PaginationContainerResponse<List<CommentResponse>>

    @GET("comment/notice/{noticeId}")
    suspend fun getNoticeComments(
        @Path("noticeId") postId: String,
        @Query("cursor") cursor: String,
        @Query("size") size: Int,
    ): PaginationContainerResponse<List<CommentResponse>>

    @POST("comment/post/{postId}")
    suspend fun createComment(
        @Path("postId") postId: String,
        @Body params: JsonObject,
    ): CommentResponse

    @POST("comment/post/{postId}/{commentId}")
    suspend fun createReplyComment(
        @Path("postId") postId: String,
        @Path("commentId") commentId: String,
        @Body params: JsonObject,
    ): CommentResponse

    @POST("comment/notice/{postId}")
    suspend fun createNoticeComment(
        @Path("postId") postId: String,
        @Body params: JsonObject,
    ): CommentResponse

    @POST("comment/post/{commentId}/likes")
    suspend fun updateLikeComment(
        @Path("commentId") commentId: String,
    ): CommentResponse

    // Common
    @PATCH("comment/{commentId}")
    suspend fun updateComment(
        @Path("commentId") commentId: String,
        @Body params: JsonObject,
    ): CommentResponse

    @POST("comment/report")
    suspend fun reportComment(
        @Body params: JsonObject,
    )

    @DELETE("comment/{commentId}")
    suspend fun deleteComment(
        @Path("commentId") commentId: String,
    )
}
