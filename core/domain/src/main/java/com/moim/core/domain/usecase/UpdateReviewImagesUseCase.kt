package com.moim.core.domain.usecase

import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.model.ReviewImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateReviewImagesUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {

    suspend operator fun invoke(params: Params): Flow<List<ReviewImage>> {
        if (params.removeImageIds.isNotEmpty()) {
            reviewRepository.deleteReviewImage(params.reviewId, params.removeImageIds).first()
        }

        return reviewRepository.updateReviewImages(reviewId = params.reviewId, params.uploadImages)
    }

    data class Params(
        val reviewId: String,
        val uploadImages: List<String>,
        val removeImageIds: List<String>,
    )
}