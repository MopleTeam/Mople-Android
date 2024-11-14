package com.moim.feature.planwrite

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.route.DetailRoute

fun NavGraphBuilder.planWriteScreen(
    padding: PaddingValues,
    navigateToBack: () -> Unit,
) {
    composable<DetailRoute.PlanWrite>(
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) }
    ) {
        PlanWriteRoute(
            padding = padding,
            navigateToBack = navigateToBack
        )
    }
}

fun NavController.navigateToPlanWrite(
    planId: String? = null,
    navOptions: NavOptions? = null
) {
    this.navigate(DetailRoute.PlanWrite(planId), navOptions)
}