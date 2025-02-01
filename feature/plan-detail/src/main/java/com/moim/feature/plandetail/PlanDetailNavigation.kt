package com.moim.feature.plandetail

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.model.item.PlanItem
import com.moim.core.route.DetailRoute

fun NavGraphBuilder.planDetailScreen(
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    navigateToPlanWrite: (PlanItem) -> Unit,
) {
    composable<DetailRoute.PlanDetail>(
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(0)) }
    ) {
        PlanDetailRoute(
            padding = padding,
            navigateToBack = navigateToBack,
            navigateToPlanWrite = navigateToPlanWrite
        )
    }
}

fun NavController.navigateToPlanDetail(
    postId: String,
    isPlan: Boolean,
    navOptions: NavOptions? = null
) {
    this.navigate(DetailRoute.PlanDetail(postId, isPlan), navOptions)
}