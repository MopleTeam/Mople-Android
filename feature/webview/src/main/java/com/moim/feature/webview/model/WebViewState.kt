package com.moim.feature.webview.model

import androidx.compose.runtime.Immutable

@Immutable
data class WebViewState(
    val webUrl: String = "",
    val webTitle: String = "",
    val loadProgress: Float = 0f,
)
