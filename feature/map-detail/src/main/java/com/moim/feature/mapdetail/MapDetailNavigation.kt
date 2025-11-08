package com.moim.feature.mapdetail

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.ui.route.DetailRoute


fun NavGraphBuilder.mapDetailScreen(
    innerPadding: PaddingValues,
    navigateToBack: () -> Unit,
) {
    composable<DetailRoute.MapDetail>(
        enterTransition = { fadeIn(animationSpec = tween(0)) },
        exitTransition = { fadeOut(animationSpec = tween(0)) }
    ) {
        MapDetailRoute(
            paddingValues = innerPadding,
            navigateToBack = navigateToBack
        )
    }
}

fun NavController.navigateToMapDetail(
    placeName: String,
    address: String,
    latitude: Double,
    longitude: Double,
    navOptions: NavOptions? = null
) {
    this.navigate(
        route = DetailRoute.MapDetail(
            placeName = placeName,
            address = address,
            latitude = latitude,
            longitude = longitude,
        ),
        navOptions = navOptions
    )
}