package com.moim.core.network.service

import com.moim.core.datamodel.CommentResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentApi {

    @GET("comment/{postId}")
    suspend fun getComments(@Path("postId") postId: String): List<CommentResponse>

    @POST("comment/{postId}")
    suspend fun createComment(
        @Path("postId") postId: String,
        @Body params: JsonObject
    ): List<CommentResponse>

    @PATCH("comment/{postId}/{commentId}")
    suspend fun updateComment(
        @Path("postId") postId: String,
        @Path("commentId") commentId: String,
        @Body params: JsonObject
    ): List<CommentResponse>

    @DELETE("comment/{commentId}")
    suspend fun deleteComment(@Path("commentId") commentId: String)

    @POST("comment/report")
    suspend fun reportComment(@Body params: JsonObject)
}