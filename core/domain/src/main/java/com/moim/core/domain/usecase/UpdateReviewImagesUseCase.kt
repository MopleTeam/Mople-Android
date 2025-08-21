package com.moim.core.domain.usecase

import com.moim.core.common.di.IoDispatcher
import com.moim.core.data.datasource.review.ReviewRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateReviewImagesUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke(params: Params): Flow<Unit> = withContext(ioDispatcher) {
        if (params.removeImageIds.isNotEmpty()) {
            reviewRepository.deleteReviewImage(params.reviewId, params.removeImageIds).first()
        }

        reviewRepository.updateReviewImages(reviewId = params.reviewId, params.uploadImages)
    }

    data class Params(
        val reviewId: String,
        val uploadImages: List<String>,
        val removeImageIds: List<String>,
    )
}