package com.moim.core.data.datasource.review

import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.Review
import com.moim.core.common.model.User

interface ReviewRepository {
    suspend fun getReviews(
        meetingId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Review>>

    suspend fun getReview(reviewId: String): Review

    suspend fun getReviewForPostId(postId: String): Review

    suspend fun getReviewParticipants(
        reviewId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<User>>

    suspend fun deleteReviewImage(
        reviewId: String,
        images: List<String>,
    )

    suspend fun deleteReview(reviewId: String)

    suspend fun reportReview(reviewId: String)

    suspend fun updateReviewImages(
        reviewId: String,
        uploadImages: List<String>,
    )
}
