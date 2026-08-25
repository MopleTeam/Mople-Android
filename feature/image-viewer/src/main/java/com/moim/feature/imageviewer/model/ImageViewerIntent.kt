package com.moim.feature.imageviewer.model

import com.moim.core.ui.mvi.Intent

sealed interface ImageViewerIntent : Intent {
    data object BackClick : ImageViewerIntent
}
