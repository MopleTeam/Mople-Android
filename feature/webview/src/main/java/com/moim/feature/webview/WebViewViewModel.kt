package com.moim.feature.webview

import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.route.DetailRoute
import com.moim.feature.webview.model.WebViewIntent
import com.moim.feature.webview.model.WebViewSideEffect
import com.moim.feature.webview.model.WebViewState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = WebViewViewModel.Factory::class)
class WebViewViewModel @AssistedInject constructor(
    @Assisted webViewRoute: DetailRoute.WebView,
) : MVIViewModel<WebViewState, WebViewSideEffect>(webViewRoute.asState()) {
    override fun onIntent(intent: Intent) {
        if (intent !is WebViewIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is WebViewIntent.BackClick -> {
                    postSideEffect(WebViewSideEffect.NavigateToBack)
                }

                is WebViewIntent.ProgressUpdate -> {
                    reduce {
                        if (state.loadProgress >= intent.progress) {
                            state
                        } else {
                            state.copy(loadProgress = intent.progress)
                        }
                    }
                }

                is WebViewIntent.WebTitleUpdate -> {
                    reduce { state.copy(webTitle = intent.title) }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(webViewRoute: DetailRoute.WebView): WebViewViewModel
    }
}

private fun DetailRoute.WebView.asState() = WebViewState(webUrl = webUrl)
