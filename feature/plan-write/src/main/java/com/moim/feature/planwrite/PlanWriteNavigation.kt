package com.moim.feature.planwrite

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.common.model.item.PlanItem
import com.moim.core.ui.route.DetailRoute

fun NavGraphBuilder.planWriteScreen(
    padding: PaddingValues,
    navigateToBack: () -> Unit,
) {
    composable<DetailRoute.PlanWrite>(
        typeMap = DetailRoute.PlanWrite.typeMap,
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
    planItem: PlanItem? = null,
    navOptions: NavOptions? = null
) {
    navigate(DetailRoute.PlanWrite(planItem), navOptions)
}