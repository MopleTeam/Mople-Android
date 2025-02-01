package com.moim.core.data.datasource.review

import com.moim.core.model.Member
import com.moim.core.model.Review
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {

    fun getReviews(meetingId : String) : Flow<List<Review>>

    fun getReview(reviewId: String): Flow<Review>

    fun getReviewParticipant(reviewId: String): Flow<List<Member>>

    fun deleteReviewImage(reviewId: String, images: List<String>) : Flow<Unit>

    fun deleteReview(reviewId: String) : Flow<Unit>

    fun reportReview(reviewId: String) : Flow<Unit>
}