package com.moim.core.data.datasource.review

import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.Review
import com.moim.core.common.model.User
import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.data.datasource.image.ImageUploadRemoteDataSource
import com.moim.core.data.util.catchFlow
import com.moim.core.remote.model.ReviewResponse
import com.moim.core.remote.model.UserResponse
import com.moim.core.remote.model.asItem
import com.moim.core.remote.service.ReviewApi
import com.moim.core.remote.util.converterException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class ReviewRepositoryImpl @Inject constructor(
    private val reviewApi: ReviewApi,
    private val imageUploadRemoteDataSource: ImageUploadRemoteDataSource
) : ReviewRepository {

    override suspend fun getReviews(
        meetingId: String,
        cursor: String,
        size: Int
    ): PaginationContainer<List<Review>> {
        return try {
            reviewApi.getReviews(
                id = meetingId,
                cursor = cursor,
                size = size
            ).asItem {
                it.map(ReviewResponse::asItem)
            }
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override fun getReview(reviewId: String) = catchFlow {
        emit(reviewApi.getReview(reviewId).asItem())
    }

    override suspend fun getReviewParticipants(
        reviewId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<User>> {
        return try {
            reviewApi.getReviewParticipant(
                id = reviewId,
                cursor = cursor,
                size = size,
            ).asItem {
                it.map(UserResponse::asItem)
            }
        } catch (e: Exception) {
            throw converterException(e)
        }
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
        emit(imageUploadRemoteDataSource.uploadReviewImages(reviewId, uploadImages, "review"))
    }

    companion object {
        private const val KEY_REVIEW_IMAGES = "reviewImages"
        private const val KEY_REVIEW_ID = "reviewId"
        private const val KEY_REASON = "reason"
    }
}