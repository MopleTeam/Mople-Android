package com.moim.core.data.datasource.review.remote

import com.moim.core.data.model.MemberResponse
import com.moim.core.data.model.ReviewResponse
import com.moim.core.data.service.ReviewApi
import com.moim.core.data.util.JsonUtil.jsonOf
import com.moim.core.data.util.converterException
import javax.inject.Inject

internal class ReviewRemoteDataSourceImpl @Inject constructor(
    private val reviewApi: ReviewApi
) : ReviewRemoteDataSource {

    override suspend fun getReviews(meetingId: String): List<ReviewResponse> {
        return try {
            reviewApi.getReviews(meetingId)
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun getReview(reviewId: String): ReviewResponse {
        return try {
            reviewApi.getReviewDetail(reviewId)
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun getReviewParticipant(reviewId: String): List<MemberResponse> {
        return try {
            reviewApi.getReviewParticipant(reviewId)
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun deleteReviewImage(reviewId: String, images: List<String>) {
        return try {
            reviewApi.deleteReviewImage(reviewId, jsonOf(KEY_REVIEW_IMAGES to images))
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    companion object {
        private const val KEY_REVIEW_IMAGES = "reviewImages"
    }
}