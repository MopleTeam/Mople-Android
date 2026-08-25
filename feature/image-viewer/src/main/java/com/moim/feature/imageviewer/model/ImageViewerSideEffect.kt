package com.moim.feature.imageviewer.model

sealed interface ImageViewerSideEffect {
    data object NavigateToBack : ImageViewerSideEffect
}
