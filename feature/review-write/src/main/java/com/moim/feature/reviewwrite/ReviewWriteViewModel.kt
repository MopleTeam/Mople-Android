package com.moim.feature.reviewwrite

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.moim.core.common.delegate.PlanItemViewModelDelegate
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.ToastMessage
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.common.view.checkState
import com.moim.core.common.view.restartableStateIn
import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.domain.usecase.UpdateReviewImagesUseCase
import com.moim.core.model.Review
import com.moim.core.model.ReviewImage
import com.moim.core.model.item.asPlanItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class ReviewWriteViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    reviewRepository: ReviewRepository,
    private val updateReviewImagesUseCase: UpdateReviewImagesUseCase,
    private val planItemViewModelDelegate: PlanItemViewModelDelegate,
) : BaseViewModel(), PlanItemViewModelDelegate by planItemViewModelDelegate {

    private val postId
        get() = savedStateHandle.get<String>(KEY_POST_ID) ?: ""

    private val isUpdated
        get() = savedStateHandle.get<Boolean>(KEY_IS_UPDATED) ?: false

    private val reviewWriteResult =
        reviewRepository.getReview(postId)
            .asResult()
            .restartableStateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            reviewWriteResult.collect { result ->
                when (result) {
                    is Result.Loading -> setUiState(ReviewWriteUiState.Loading)

                    is Result.Success -> {
                        val images = result.data.images

                        setUiState(
                            ReviewWriteUiState.Success(
                                review = result.data,
                                isUpdated = isUpdated,
                                uploadImages = images,
                                removeImageIds = emptyList(),
                                enableSubmit = images.isNotEmpty()
                            )
                        )
                    }

                    is Result.Error -> setUiState(ReviewWriteUiState.Error)
                }
            }
        }
    }

    fun onUiAction(uiAction: ReviewWriteUiAction) {
        when (uiAction) {
            is ReviewWriteUiAction.OnClickBack -> setUiEvent(ReviewWriteUiEvent.NavigateToBack)
            is ReviewWriteUiAction.OnClickRefresh -> reviewWriteResult.restart()
            is ReviewWriteUiAction.OnClickImageUpload -> setUiEvent(ReviewWriteUiEvent.NavigateToPhotoPicker)
            is ReviewWriteUiAction.OnClickAddImages -> addUploadImages(uiAction.imageUrls)
            is ReviewWriteUiAction.OnClickRemoveImage -> removeUploadImages(uiAction.reviewImage)
            is ReviewWriteUiAction.OnClickParticipants -> setUiEvent(ReviewWriteUiEvent.NavigateToParticipants(postId))
            is ReviewWriteUiAction.OnClickSubmit -> submitReviewImages()
        }
    }

    private fun addUploadImages(images: List<String>) {
        uiState.checkState<ReviewWriteUiState.Success> {
            val addImages = images.map {
                ReviewImage(
                    imageId = "",
                    imageUrl = it
                )
            }

            setUiState(
                copy(
                    uploadImages = uploadImages + addImages,
                    enableSubmit = true
                )
            )
        }
    }

    private fun removeUploadImages(image: ReviewImage) {
        uiState.checkState<ReviewWriteUiState.Success> {
            val uploadImages = uploadImages.toMutableList().apply { removeIf { it.imageUrl == image.imageUrl } }
            val removeImageIds = removeImageIds.toMutableList().apply { if (image.imageId.isNotEmpty()) add(image.imageId) }

            setUiState(
                copy(
                    uploadImages = uploadImages,
                    removeImageIds = removeImageIds,
                    enableSubmit = uploadImages.isNotEmpty() || removeImageIds.isNotEmpty()
                )
            )
        }
    }

    private fun submitReviewImages() {
        viewModelScope.launch {
            uiState.checkState<ReviewWriteUiState.Success> {
                updateReviewImagesUseCase(
                    UpdateReviewImagesUseCase.Params(
                        reviewId = review.reviewId,
                        uploadImages = uploadImages.map { it.imageUrl },
                        removeImageIds = removeImageIds
                    )
                )
                    .asResult()
                    .onEach { setLoading(it is Result.Loading) }
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> return@collect
                            is Result.Success -> {
                                val removeImages = review.images.toMutableList().apply { removeAll { removeImageIds.any { id -> id == it.imageId } } }
                                updatePlanItem(ZonedDateTime.now(), review.asPlanItem().copy(reviewImages = removeImages + result.data))
                                setUiEvent(ReviewWriteUiEvent.NavigateToBack)
                            }

                            is Result.Error -> when (result.exception) {
                                is IOException -> setUiEvent(ReviewWriteUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
                                else -> setUiEvent(ReviewWriteUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
                            }
                        }
                    }
            }
        }
    }

    companion object {
        private const val KEY_POST_ID = "postId"
        private const val KEY_IS_UPDATED = "isUpdated"
    }
}

sealed interface ReviewWriteUiState : UiState {
    data object Loading : ReviewWriteUiState

    data class Success(
        val review: Review,
        val isUpdated: Boolean,
        val uploadImages: List<ReviewImage>,
        val removeImageIds: List<String>,
        val enableSubmit: Boolean,
    ) : ReviewWriteUiState

    data object Error : ReviewWriteUiState
}

sealed interface ReviewWriteUiAction : UiAction {
    data object OnClickBack : ReviewWriteUiAction
    data object OnClickRefresh : ReviewWriteUiAction
    data object OnClickImageUpload : ReviewWriteUiAction
    data class OnClickAddImages(val imageUrls: List<String>) : ReviewWriteUiAction
    data class OnClickRemoveImage(val reviewImage: ReviewImage) : ReviewWriteUiAction
    data object OnClickParticipants : ReviewWriteUiAction
    data object OnClickSubmit : ReviewWriteUiAction
}

sealed interface ReviewWriteUiEvent : UiEvent {
    data object NavigateToBack : ReviewWriteUiEvent
    data object NavigateToPhotoPicker : ReviewWriteUiEvent
    data class NavigateToParticipants(val postId: String) : ReviewWriteUiEvent
    data class ShowToastMessage(val toastMessage: ToastMessage) : ReviewWriteUiEvent
}