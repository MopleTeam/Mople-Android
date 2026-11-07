package com.moim.feature.reviewwrite

import com.moim.core.common.model.ReviewImage
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.result.Result
import com.moim.core.common.result.data
import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.domain.usecase.UpdateReviewImagesUseCase
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.PlanAction
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.view.ToastMessage
import com.moim.feature.reviewwrite.model.ReviewWriteIntent
import com.moim.feature.reviewwrite.model.ReviewWriteSideEffect
import com.moim.feature.reviewwrite.model.ReviewWriteState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import org.orbitmvi.orbit.syntax.Syntax
import java.io.IOException

@HiltViewModel(assistedFactory = ReviewWriteViewModel.Factory::class)
class ReviewWriteViewModel @AssistedInject constructor(
    private val reviewRepository: ReviewRepository,
    private val updateReviewImagesUseCase: UpdateReviewImagesUseCase,
    private val planEventBus: EventBus<PlanAction>,
    @Assisted val reviewWriteRoute: DetailRoute.ReviewWrite,
) : MVIViewModel<ReviewWriteState, ReviewWriteSideEffect>(reviewWriteRoute.asState()) {
    private val postId = reviewWriteRoute.postId

    override suspend fun Syntax<ReviewWriteState, ReviewWriteSideEffect>.onContainerCreate() {
        loadReview()
    }

    override fun onIntent(intent: Intent) {
        if (intent !is ReviewWriteIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is ReviewWriteIntent.BackClick -> {
                    postSideEffect(ReviewWriteSideEffect.NavigateToBack)
                }

                is ReviewWriteIntent.RefreshClick -> {
                    loadReview()
                }

                is ReviewWriteIntent.ImageUploadClick -> {
                    postSideEffect(ReviewWriteSideEffect.NavigateToPhotoPicker)
                }

                is ReviewWriteIntent.ParticipantsClick -> {
                    postSideEffect(ReviewWriteSideEffect.NavigateToParticipants(ViewIdType.ReviewId(postId)))
                }

                is ReviewWriteIntent.SubmitClick -> {
                    submitReviewImages()
                }

                is ReviewWriteIntent.ImagesAdd -> {
                    addUploadImages(intent.imageUrls)
                }

                is ReviewWriteIntent.ImageRemoveClick -> {
                    removeImage(intent.reviewImage)
                }
            }
        }
    }

    private fun loadReview() {
        intent {
            reduce { state.copy(review = Result.Loading) }

            try {
                val review = reviewRepository.getReview(postId)

                reduce {
                    state.copy(
                        review = Result.Success(review),
                        uploadImages = review.images,
                        removeImageIds = emptyList(),
                        enableSubmit = review.images.isNotEmpty(),
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                reduce { state.copy(review = Result.Error(e)) }
            }
        }
    }

    private suspend fun Syntax<ReviewWriteState, ReviewWriteSideEffect>.addUploadImages(images: List<String>) {
        val addImages =
            images.map {
                ReviewImage(
                    imageId = "",
                    imageUrl = it,
                )
            }

        reduce {
            state.copy(
                uploadImages = state.uploadImages + addImages,
                enableSubmit = true,
            )
        }
    }

    private suspend fun Syntax<ReviewWriteState, ReviewWriteSideEffect>.removeImage(image: ReviewImage) {
        val uploadImages = state.uploadImages.filterNot { it.imageUrl == image.imageUrl }
        val removeImageIds =
            state.removeImageIds.toMutableList().apply { if (image.imageId.isNotEmpty()) add(image.imageId) }

        reduce {
            state.copy(
                uploadImages = uploadImages,
                removeImageIds = removeImageIds,
                enableSubmit = uploadImages.isNotEmpty() || removeImageIds.isNotEmpty(),
            )
        }
    }

    private suspend fun Syntax<ReviewWriteState, ReviewWriteSideEffect>.submitReviewImages() {
        val review = state.review.data ?: return
        val uploadImages = state.uploadImages.map { it.imageUrl }
        val removeImageIds = state.removeImageIds

        setLoading(true)

        try {
            updateReviewImagesUseCase(
                UpdateReviewImagesUseCase.Params(
                    reviewId = review.reviewId,
                    uploadImages = uploadImages,
                    removeImageIds = removeImageIds,
                ),
            )

            planEventBus.send(PlanAction.PlanInvalidate())
            postSideEffect(ReviewWriteSideEffect.NavigateToBack)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<ReviewWriteState, ReviewWriteSideEffect>.showErrorToast(exception: Throwable) {
        val message = if (exception is IOException) ToastMessage.NetworkErrorMessage else ToastMessage.ServerErrorMessage
        postSideEffect(ReviewWriteSideEffect.ShowToastMessage(message))
    }

    @AssistedFactory
    interface Factory {
        fun create(reviewWriteRoute: DetailRoute.ReviewWrite): ReviewWriteViewModel
    }
}

private fun DetailRoute.ReviewWrite.asState() = ReviewWriteState(isUpdated = isUpdated)
