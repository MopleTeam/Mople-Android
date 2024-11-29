package com.moim.core.data.datasource.review.remote

import com.moim.core.data.model.MemberResponse
import com.moim.core.data.model.ReviewResponse

internal interface ReviewRemoteDataSource {

    suspend fun getReviews(meetingId : String) : List<ReviewResponse>

    suspend fun getReview(reviewId: String): ReviewResponse

    suspend fun getReviewParticipant(reviewId: String): List<MemberResponse>

    suspend fun submitReviewFeedReport(reviewId: String, reason: String)

    suspend fun deleteReviewImage(reviewId: String, images: List<String>)
}