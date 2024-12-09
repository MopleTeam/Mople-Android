package com.moim.core.data.datasource.review

import com.moim.core.datamodel.MemberResponse
import com.moim.core.datamodel.ReviewResponse
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {

    fun getReviews(meetingId : String) : Flow<List<ReviewResponse>>

    fun getReview(reviewId: String): Flow<ReviewResponse>

    fun getReviewParticipant(reviewId: String): Flow<List<MemberResponse>>

    fun submitReviewFeedReport(reviewId: String, reason: String) : Flow<Unit>

    fun deleteReviewImage(reviewId: String, images: List<String>) : Flow<Unit>
}