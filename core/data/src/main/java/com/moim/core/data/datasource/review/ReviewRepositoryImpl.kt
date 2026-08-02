package com.moim.core.data.datasource.review

import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.Review
import com.moim.core.common.model.User
import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.remote.datasource.image.ImageUploadRemoteDataSource
import com.moim.core.remote.datasource.review.ReviewRemoteDataSource
import com.moim.core.remote.model.ReviewResponse
import com.moim.core.remote.model.UserResponse
import com.moim.core.remote.model.asItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class ReviewRepositoryImpl @Inject constructor(
    private val reviewRemoteDataSource: ReviewRemoteDataSource,
    private val imageUploadRemoteDataSource: ImageUploadRemoteDataSource,
) : ReviewRepository {
    override suspend fun getReviews(
        meetingId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Review>> =
        reviewRemoteDataSource
            .getReviews(
                id = meetingId,
                cursor = cursor,
                size = size,
            ).asItem {
                it.map(ReviewResponse::asItem)
            }

    override fun getReview(reviewId: String) =
        flow {
            emit(reviewRemoteDataSource.getReview(reviewId).asItem())
        }

    override fun getReviewForPostId(postId: String) =
        flow {
            emit(reviewRemoteDataSource.gerReviewForPostId(postId).asItem())
        }

    override suspend fun getReviewParticipants(
        reviewId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<User>> =
        reviewRemoteDataSource
            .getReviewParticipant(
                id = reviewId,
                cursor = cursor,
                size = size,
            ).asItem {
                it.map(UserResponse::asItem)
            }

    override fun deleteReviewImage(
        reviewId: String,
        images: List<String>,
    ): Flow<Unit> =
        flow {
            emit(reviewRemoteDataSource.deleteReviewImage(reviewId, jsonOf(KEY_REVIEW_IMAGES to images)))
        }

    override fun deleteReview(reviewId: String) =
        flow {
            emit(reviewRemoteDataSource.deleteReview(reviewId))
        }

    override fun reportReview(reviewId: String) =
        flow {
            emit(
                reviewRemoteDataSource.reportReview(
                    jsonOf(
                        KEY_REVIEW_ID to reviewId,
                        KEY_REASON to "",
                    ),
                ),
            )
        }

    override fun updateReviewImages(
        reviewId: String,
        uploadImages: List<String>,
    ) = flow {
        emit(imageUploadRemoteDataSource.uploadReviewImages(reviewId, uploadImages, "review"))
    }

    companion object {
        private const val KEY_REVIEW_IMAGES = "reviewImages"
        private const val KEY_REVIEW_ID = "reviewId"
        private const val KEY_REASON = "reason"
    }
}
