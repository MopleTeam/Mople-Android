package com.moim.feature.imageviewer.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable

@Immutable
data class ImageViewerState(
    val title: String = "",
    val images: List<String> = emptyList(),
    val position: Int = 0,
    @DrawableRes val defaultImage: Int? = null,
)
