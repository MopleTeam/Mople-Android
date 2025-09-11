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

    @GET("comment/{postId}")
    suspend fun getComments(
        @Path("postId") postId: String,
        @Query("cursor") cursor: String,
        @Query("size") size: Int,
    ): PaginationContainerResponse<List<CommentResponse>>

    @GET("comment/{postId}/{commentId}")
    suspend fun getReplyComments(
        @Path("postId") postId: String,
        @Path("commentId") commentId: String,
        @Query("cursor") cursor: String,
        @Query("size") size: Int,
    ): PaginationContainerResponse<List<CommentResponse>>

    @POST("comment/{postId}")
    suspend fun createComment(
        @Path("postId") postId: String,
        @Body params: JsonObject
    ): CommentResponse

    @POST("comment/{postId}/{commentId}")
    suspend fun createReplyComment(
        @Path("postId") postId: String,
        @Path("commentId") commentId: String,
        @Body params: JsonObject
    ): CommentResponse

    @PATCH("comment/{commentId}")
    suspend fun updateComment(
        @Path("commentId") commentId: String,
        @Body params: JsonObject
    ): CommentResponse

    @POST("comment/{commentId}/likes")
    suspend fun updateLikeComment(
        @Path("commentId") commentId: String,
    ): CommentResponse

    @DELETE("comment/{commentId}")
    suspend fun deleteComment(@Path("commentId") commentId: String)

    @POST("comment/report")
    suspend fun reportComment(@Body params: JsonObject)
}