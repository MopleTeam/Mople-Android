package com.moim.feature.imageviewer

import androidx.annotation.DrawableRes
import androidx.lifecycle.SavedStateHandle
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageViewerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    private val title
        get() = savedStateHandle.get<String>(KEY_TITLE) ?: ""

    private val images
        get() = savedStateHandle.get<Array<String>>(KEY_IMAGES)?.toList() ?: emptyList()

    private val currentPosition
        get() = savedStateHandle.get<Int>(KEY_POSITION) ?: 0

    private val defaultImage
        get() = savedStateHandle.get<Int?>(KEY_DEFAULT_IMAGE)

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

    companion object {
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGES = "images"
        private const val KEY_POSITION = "position"
        private const val KEY_DEFAULT_IMAGE = "defaultImage"
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