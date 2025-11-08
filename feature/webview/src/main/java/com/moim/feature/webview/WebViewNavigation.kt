package com.moim.feature.webview

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.ui.route.DetailRoute

fun NavGraphBuilder.webViewScreen(
    padding: PaddingValues,
    navigateToBack: () -> Unit,
) {
    composable<DetailRoute.WebView>(
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) }
    ) {
        WebViewRoute(
            padding = padding,
            navigateToBack = navigateToBack
        )
    }
}

fun NavController.navigateToWebView(
    webUrl: String,
    navOptions: NavOptions? = null
) {
    navigate(DetailRoute.WebView(webUrl), navOptions)
}