package com.moim.core.data.datasource.review

import com.moim.core.model.Member
import com.moim.core.model.Review
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {

    fun getReviews(meetingId : String) : Flow<List<Review>>

    fun getReview(reviewId: String): Flow<Review>

    fun getReviewParticipant(reviewId: String): Flow<List<Member>>

    fun submitReviewFeedReport(reviewId: String, reason: String) : Flow<Unit>

    fun deleteReviewImage(reviewId: String, images: List<String>) : Flow<Unit>
}