package com.moim.core.data.datasource.review

import com.moim.core.data.datasource.review.remote.ReviewRemoteDataSource
import com.moim.core.datamodel.MemberResponse
import com.moim.core.datamodel.ReviewResponse
import com.moim.core.model.asItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class ReviewRepositoryImpl @Inject constructor(
    private val remoteDataSource: ReviewRemoteDataSource
) : ReviewRepository {

    override fun getReviews(meetingId: String) = flow {
        emit(remoteDataSource.getReviews(meetingId).map(ReviewResponse::asItem))
    }

    override fun getReview(reviewId: String) = flow {
        emit(remoteDataSource.getReview(reviewId).asItem())
    }

    override fun getReviewParticipant(reviewId: String) = flow {
        emit(remoteDataSource.getReviewParticipant(reviewId).map(MemberResponse::asItem))
    }

    override fun deleteReviewImage(reviewId: String, images: List<String>): Flow<Unit> = flow {
        emit(remoteDataSource.deleteReviewImage(reviewId, images))
    }

    override fun deleteReview(reviewId: String) = flow {
        emit(remoteDataSource.deleteReview(reviewId))
    }

    override fun reportReview(reviewId: String) = flow {
        emit(remoteDataSource.reportReview(reviewId))
    }
}