package com.moim.core.data.datasource.review.remote

import com.moim.core.datamodel.MemberResponse
import com.moim.core.datamodel.ReviewResponse

internal interface ReviewRemoteDataSource {

    suspend fun getReviews(meetingId : String) : List<ReviewResponse>

    suspend fun getReview(reviewId: String): ReviewResponse

    suspend fun getReviewParticipant(reviewId: String): List<MemberResponse>

    suspend fun deleteReviewImage(reviewId: String, images: List<String>)

    suspend fun deleteReview(reviewId: String)

    suspend fun reportReview(reviewId: String)
}