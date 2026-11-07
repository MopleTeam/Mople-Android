package com.moim.feature.imageviewer

import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.route.DetailRoute
import com.moim.feature.imageviewer.model.ImageViewerIntent
import com.moim.feature.imageviewer.model.ImageViewerSideEffect
import com.moim.feature.imageviewer.model.ImageViewerState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = ImageViewerViewModel.Factory::class)
class ImageViewerViewModel @AssistedInject constructor(
    @Assisted imageViewerRoute: DetailRoute.ImageViewer,
) : MVIViewModel<ImageViewerState, ImageViewerSideEffect>(imageViewerRoute.asState()) {
    override fun onIntent(intent: Intent) {
        if (intent !is ImageViewerIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is ImageViewerIntent.BackClick -> {
                    postSideEffect(ImageViewerSideEffect.NavigateToBack)
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(imageViewerRoute: DetailRoute.ImageViewer): ImageViewerViewModel
    }
}

private fun DetailRoute.ImageViewer.asState() =
    ImageViewerState(
        title = title,
        images = images,
        position = position,
        defaultImage = defaultImage,
    )
