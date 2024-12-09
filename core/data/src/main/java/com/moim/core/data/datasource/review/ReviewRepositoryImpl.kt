package com.moim.core.data.datasource.review

import com.moim.core.data.datasource.review.remote.ReviewRemoteDataSource
import com.moim.core.datamodel.MemberResponse
import com.moim.core.datamodel.ReviewResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class ReviewRepositoryImpl @Inject constructor(
    private val remoteDataSource: ReviewRemoteDataSource
) : ReviewRepository {

    override fun getReviews(meetingId: String): Flow<List<ReviewResponse>> = flow {
        emit(remoteDataSource.getReviews(meetingId))
    }

    override fun getReview(reviewId: String): Flow<ReviewResponse> = flow {
        emit(remoteDataSource.getReview(reviewId))
    }

    override fun getReviewParticipant(reviewId: String): Flow<List<MemberResponse>> = flow {
        emit(remoteDataSource.getReviewParticipant(reviewId))
    }

    override fun submitReviewFeedReport(reviewId: String, reason: String): Flow<Unit> = flow {
        emit(remoteDataSource.submitReviewFeedReport(reviewId, reason))
    }

    override fun deleteReviewImage(reviewId: String, images: List<String>): Flow<Unit> = flow {
        emit(remoteDataSource.deleteReviewImage(reviewId, images))
    }
}