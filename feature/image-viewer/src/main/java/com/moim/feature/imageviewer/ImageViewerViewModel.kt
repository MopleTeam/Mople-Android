package com.moim.feature.imageviewer

import androidx.annotation.DrawableRes
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = ImageViewerViewModel.Factory::class)
class ImageViewerViewModel @AssistedInject constructor(
    @Assisted imageViewer: DetailRoute.ImageViewer,
) : BaseViewModel() {
    private val title = imageViewer.title
    private val images = imageViewer.images
    private val currentPosition = imageViewer.position
    private val defaultImage = imageViewer.defaultImage

    init {
        setUiState(
            ImageViewerUiState(
                title = title,
                images = images,
                position = currentPosition,
                defaultImage = defaultImage
            )
        )
    }

    fun onUiAction(uiAction: ImageViewerUiAction) {
        when (uiAction) {
            is ImageViewerUiAction.OnClickBack -> setUiEvent(ImageViewerUiEvent.NavigateToBack)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            commentDetail: DetailRoute.ImageViewer
        ): ImageViewerViewModel
    }
}

data class ImageViewerUiState(
    val title: String,
    val images: List<String>,
    val position: Int,
    @DrawableRes val defaultImage: Int? = null,
) : UiState

sealed interface ImageViewerUiAction : UiAction {
    data object OnClickBack : ImageViewerUiAction
}

sealed interface ImageViewerUiEvent : UiEvent {
    data object NavigateToBack : ImageViewerUiEvent
}