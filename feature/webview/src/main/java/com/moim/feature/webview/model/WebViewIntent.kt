package com.moim.feature.webview.model

import com.moim.core.ui.mvi.Intent

sealed interface WebViewIntent : Intent {
    data object BackClick : WebViewIntent

    data class ProgressUpdate(
        val progress: Float,
    ) : WebViewIntent

    data class WebTitleUpdate(
        val title: String,
    ) : WebViewIntent
}
