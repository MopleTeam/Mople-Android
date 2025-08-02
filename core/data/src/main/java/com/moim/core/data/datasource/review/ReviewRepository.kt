package com.moim.core.data.datasource.review

import com.moim.core.model.PaginationContainer
import com.moim.core.model.Participant
import com.moim.core.model.Review
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {

    suspend fun getReviews(
        meetingId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Review>>

    fun getReview(reviewId: String): Flow<Review>

    fun getReviewParticipants(reviewId: String): Flow<List<Participant>>

    fun deleteReviewImage(reviewId: String, images: List<String>): Flow<Unit>

    fun deleteReview(reviewId: String): Flow<Unit>

    fun reportReview(reviewId: String): Flow<Unit>

    fun updateReviewImages(
        reviewId: String,
        uploadImages: List<String>,
    ): Flow<Unit>
}