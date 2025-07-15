package com.moim.core.data.datasource.review

import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.data.datasource.image.ImageUploadRemoteDataSource
import com.moim.core.data.mapper.asItem
import com.moim.core.data.util.catchFlow
import com.moim.core.datamodel.ReviewResponse
import com.moim.core.network.service.ReviewApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class ReviewRepositoryImpl @Inject constructor(
    private val reviewApi: ReviewApi,
    private val imageUploadRemoteDataSource: ImageUploadRemoteDataSource
) : ReviewRepository {

    override fun getReviews(meetingId: String) = catchFlow {
        emit(reviewApi.getReviews(meetingId).map(ReviewResponse::asItem))
    }

    override fun getReview(reviewId: String) = catchFlow {
        emit(reviewApi.getReview(reviewId).asItem())
    }

    override fun getReviewParticipants(reviewId: String) = catchFlow {
        val reviewParticipants = reviewApi.getReviewParticipant(reviewId)
        emit(reviewParticipants.members.map { it.asItem(reviewParticipants.creatorId == it.memberId) })
    }

    override fun deleteReviewImage(reviewId: String, images: List<String>): Flow<Unit> = catchFlow {
        emit(reviewApi.deleteReviewImage(reviewId, jsonOf(KEY_REVIEW_IMAGES to images)))
    }

    override fun deleteReview(reviewId: String) = catchFlow {
        emit(reviewApi.deleteReview(reviewId))
    }

    override fun reportReview(reviewId: String) = catchFlow {
        emit(
            reviewApi.reportReview(
                jsonOf(
                    KEY_REVIEW_ID to reviewId,
                    KEY_REASON to ""
                )
            )
        )
    }

    override fun updateReviewImages(
        reviewId: String,
        uploadImages: List<String>,
    ) = catchFlow {
        emit(imageUploadRemoteDataSource.uploadReviewImages(reviewId, uploadImages, "review_image"))
    }

    companion object {
        private const val KEY_REVIEW_IMAGES = "reviewImages"
        private const val KEY_REVIEW_ID = "reviewId"
        private const val KEY_REASON = "reason"
    }
}