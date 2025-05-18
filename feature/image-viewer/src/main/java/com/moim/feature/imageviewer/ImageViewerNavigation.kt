package com.moim.feature.imageviewer

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.route.DetailRoute

fun NavGraphBuilder.imageViewerScreen(
    padding: PaddingValues,
    navigateToBack: () -> Unit,
) {
    composable<DetailRoute.ImageViewer>(
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) }
    ) {
        ImageViewerRoute(
            padding = padding,
            navigateToBack = navigateToBack
        )
    }
}

fun NavController.navigateToImageViewer(
    title: String,
    images: List<String>,
    position: Int,
    @DrawableRes defaultImage: Int? = null,
    navOptions: NavOptions? = null
) {
    this.navigate(
        route = DetailRoute.ImageViewer(
            title = title,
            images = images,
            position = position,
            defaultImage = defaultImage
        ),
        navOptions = navOptions
    )
}