package com.moim.feature.webview

import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = WebViewViewModel.Factory::class)
class WebViewViewModel @AssistedInject constructor(
    @Assisted val webUrl: String,
) : BaseViewModel() {

    init {
        setUiState(WebViewUiState(webUrl = webUrl, loadProgress = 0f))
    }

    fun onUiAction(uiAction: WebViewUiAction) {
        when (uiAction) {
            is WebViewUiAction.OnClickBack -> setUiEvent(WebViewUiEvent.NavigateToBack)
            is WebViewUiAction.UpdatedProgress -> setProgress(uiAction.progress)
            is WebViewUiAction.UpdatedWebTitle -> setWebViewTitle(uiAction.title)
        }
    }

    private fun setProgress(progress: Float) {
        uiState.checkState<WebViewUiState> {
            if (loadProgress >= progress) return@checkState
            setUiState(copy(loadProgress = progress))
        }
    }

    private fun setWebViewTitle(title: String) {
        uiState.checkState<WebViewUiState> {
            setUiState(copy(webTitle = title))
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            webUrl: String,
        ): WebViewViewModel
    }
}

data class WebViewUiState(
    val webUrl: String = "",
    val webTitle: String = "",
    val loadProgress: Float = 0f,
) : UiState

sealed interface WebViewUiAction : UiAction {
    data object OnClickBack : WebViewUiAction

    data class UpdatedProgress(
        val progress: Float,
    ) : WebViewUiAction

    data class UpdatedWebTitle(
        val title: String,
    ) : WebViewUiAction
}

sealed interface WebViewUiEvent : UiEvent {
    data object NavigateToBack : WebViewUiEvent
}