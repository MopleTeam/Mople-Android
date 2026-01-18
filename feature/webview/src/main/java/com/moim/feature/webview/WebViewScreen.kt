package com.moim.feature.webview

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.view.ObserveAsEvents
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun WebViewRoute(
    viewModel: WebViewViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToBack: () -> Unit,
) {
    val modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.bg.primary, padding = padding)
    val webViewUiState by viewModel.uiState
        .filterIsInstance<WebViewUiState>()
        .collectAsStateWithLifecycle(WebViewUiState())

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is WebViewUiEvent.NavigateToBack -> navigateToBack()
        }
    }

    WebViewScreen(
        modifier = modifier,
        uiState = webViewUiState,
        onUiAction = viewModel::onUiAction,
    )
}

@Composable
fun WebViewScreen(
    modifier: Modifier = Modifier,
    uiState: WebViewUiState,
    onUiAction: (WebViewUiAction) -> Unit,
) {
    MoimScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                MoimTopAppbar(
                    modifier = Modifier.fillMaxWidth(),
                    title = uiState.webTitle,
                    onClickNavigate = { onUiAction(WebViewUiAction.OnClickBack) },
                )
                AnimatedVisibility(uiState.loadProgress < 1f) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        trackColor = MoimTheme.colors.primary.disable,
                        color = MoimTheme.colors.global.primary,
                        progress = { uiState.loadProgress },
                        drawStopIndicator = {},
                    )
                }
            }
        },
        content = {
            WebViewContainer(
                modifier = Modifier.padding(it),
                webUrl = uiState.webUrl,
                onProgress = { onUiAction(WebViewUiAction.UpdatedProgress(it)) },
                onWebTitle = { onUiAction(WebViewUiAction.UpdatedWebTitle(it)) },
            )
        },
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebViewContainer(
    modifier: Modifier = Modifier,
    webUrl: String,
    onProgress: (Float) -> Unit = {},
    onWebTitle: (String) -> Unit = {},
) {
    var isLoadingView by remember { mutableStateOf(true) }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { WebView(it) },
    ) { webView ->
        webView.settings.apply {
            defaultTextEncodingName = "UTF-8"
            builtInZoomControls = false
            displayZoomControls = false
            javaScriptEnabled = true
            domStorageEnabled = true
        }
        webView.apply {
            layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
            webViewClient =
                object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?,
                    ): Boolean = super.shouldOverrideUrlLoading(view, request)

                    override fun onPageFinished(
                        view: WebView?,
                        url: String?,
                    ) {
                        isLoadingView = false
                        onProgress(1f)
                        super.onPageFinished(view, url)
                    }
                }

            webChromeClient =
                object : WebChromeClient() {
                    override fun onProgressChanged(
                        view: WebView?,
                        newProgress: Int,
                    ) {
                        isLoadingView = newProgress < 100
                        onProgress(newProgress / 100f)
                        super.onProgressChanged(view, newProgress)
                    }

                    override fun onReceivedTitle(
                        view: WebView,
                        title: String,
                    ) {
                        onWebTitle(title)
                        super.onReceivedTitle(view, title)
                    }
                }
        }

        webView.webViewClient = WebViewClient()
        webView.loadUrl(webUrl)
    }
}

@Preview
@Composable
private fun WebViewScreenPreview() {
    MoimTheme {
        WebViewScreen(
            modifier = Modifier.containerScreen(),
            uiState = WebViewUiState(webUrl = "", webTitle = "공지사항"),
            onUiAction = {},
        )
    }
}
