package com.moim.feature.reviewwrite

import androidx.lifecycle.viewModelScope
import com.moim.core.common.model.Review
import com.moim.core.common.model.ReviewImage
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.domain.usecase.UpdateReviewImagesUseCase
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.PlanAction
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.ToastMessage
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import com.moim.core.ui.view.restartableStateIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException

@HiltViewModel(assistedFactory = ReviewWriteViewModel.Factory::class)
class ReviewWriteViewModel @AssistedInject constructor(
    reviewRepository: ReviewRepository,
    private val updateReviewImagesUseCase: UpdateReviewImagesUseCase,
    private val planEventBus: EventBus<PlanAction>,
    @Assisted val postId: String,
    @Assisted val isUpdated: Boolean,
) : BaseViewModel() {

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
            is ReviewWriteUiAction.OnClickRemoveImage -> removeImages(uiAction.reviewImage)
            is ReviewWriteUiAction.OnClickParticipants -> setUiEvent(ReviewWriteUiEvent.NavigateToParticipants(ViewIdType.ReviewId(postId)))
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

    private fun removeImages(image: ReviewImage) {
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
                                planEventBus.send(PlanAction.PlanInvalidate())
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

    @AssistedFactory
    interface Factory {
        fun create(
            postId: String,
            isUpdated: Boolean,
        ): ReviewWriteViewModel
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

    data class OnClickAddImages(
        val imageUrls: List<String>
    ) : ReviewWriteUiAction

    data class OnClickRemoveImage(
        val reviewImage: ReviewImage
    ) : ReviewWriteUiAction

    data object OnClickParticipants : ReviewWriteUiAction

    data object OnClickSubmit : ReviewWriteUiAction
}

sealed interface ReviewWriteUiEvent : UiEvent {
    data object NavigateToBack : ReviewWriteUiEvent

    data object NavigateToPhotoPicker : ReviewWriteUiEvent

    data class NavigateToParticipants(
        val viewIdType: ViewIdType
    ) : ReviewWriteUiEvent

    data class ShowToastMessage(
        val toastMessage: ToastMessage
    ) : ReviewWriteUiEvent
}