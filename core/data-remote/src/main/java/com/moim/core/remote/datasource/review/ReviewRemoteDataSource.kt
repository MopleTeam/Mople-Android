package com.moim.core.remote.datasource.review

import com.moim.core.remote.model.PaginationContainerResponse
import com.moim.core.remote.model.ReviewResponse
import com.moim.core.remote.model.UserResponse
import kotlinx.serialization.json.JsonObject

interface ReviewRemoteDataSource {
    suspend fun getReviews(
        id: String,
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<ReviewResponse>>

    suspend fun getReview(id: String): ReviewResponse

    suspend fun gerReviewForPostId(id: String): ReviewResponse

    suspend fun getReviewParticipant(
        id: String,
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<UserResponse>>

    suspend fun deleteReviewImage(
        id: String,
        params: JsonObject,
    )

    suspend fun deleteReview(id: String)

    suspend fun reportReview(params: JsonObject)
}
