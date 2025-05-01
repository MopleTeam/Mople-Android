package com.moim.feature.webview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.common.view.checkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebViewViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val webUrl: String
        get() = savedStateHandle.get<String>(KEY_WEB_URL) ?: ""

    init {
        viewModelScope.launch {
            setUiState(WebViewUiState(webUrl = webUrl, loadProgress = 0f))
        }
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

    companion object {
        private const val KEY_WEB_URL = "webUrl"
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