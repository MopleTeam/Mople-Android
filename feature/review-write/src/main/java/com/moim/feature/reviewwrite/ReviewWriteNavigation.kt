package com.moim.feature.reviewwrite

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.common.route.DetailRoute

fun NavGraphBuilder.reviewWriteScreen(
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    navigateToParticipants: (isMeeting: Boolean, isPlan: Boolean, id: String) -> Unit,
) {
    composable<DetailRoute.ReviewWrite>(
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) }
    ) {
        ReviewWriteRoute(
            padding = padding,
            navigateToBack = navigateToBack,
            navigateToParticipants = navigateToParticipants
        )
    }
}

fun NavController.navigateToReviewWrite(
    postId: String,
    isUpdated: Boolean,
    navOptions: NavOptions? = null
) {
    navigate(DetailRoute.ReviewWrite(postId, isUpdated), navOptions)
}