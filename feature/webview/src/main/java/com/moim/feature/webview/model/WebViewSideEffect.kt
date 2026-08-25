package com.moim.feature.webview.model

sealed interface WebViewSideEffect {
    data object NavigateToBack : WebViewSideEffect
}
