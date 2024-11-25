package com.moim.core.data.service

import com.moim.core.data.model.MemberResponse
import com.moim.core.data.model.ReviewResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

internal interface ReviewApi {

    @GET("/review/list/{meetId}")
    suspend fun getReviews(@Path("meetId") id: String): List<ReviewResponse>

    @GET("/review/{reviewId}")
    suspend fun getReviewDetail(@Path("reviewId") id: String): ReviewResponse

    @GET("/review/participant/{reviewId}")
    suspend fun getReviewParticipant(@Path("reviewId") id: String): List<MemberResponse>

    @DELETE("/review/images/{reviewId}")
    suspend fun deleteReviewImage(@Path("reviewId") id: String, @Body params: JsonObject)

}