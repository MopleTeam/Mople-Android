package com.moim.core.network.service

import com.moim.core.datamodel.PaginationContainerResponse
import com.moim.core.datamodel.ParticipantContainerResponse
import com.moim.core.datamodel.ReviewResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReviewApi {

    @GET("review/list/{meetId}")
    suspend fun getReviews(
        @Path("meetId") id: String,
        @Query("cursor") cursor: String,
        @Query("size") size: Int,
    ): PaginationContainerResponse<List<ReviewResponse>>

    @GET("review/{reviewId}")
    suspend fun getReview(@Path("reviewId") id: String): ReviewResponse

    @GET("review/participant/{reviewId}")
    suspend fun getReviewParticipant(@Path("reviewId") id: String): ParticipantContainerResponse

    @HTTP(method = "DELETE", path = "review/images/{reviewId}", hasBody = true)
    suspend fun deleteReviewImage(@Path("reviewId") id: String, @Body params: JsonObject)

    @DELETE("review/{reviewId}")
    suspend fun deleteReview(@Path("reviewId") id: String)

    @POST("review/report")
    suspend fun reportReview(@Body params: JsonObject)
}