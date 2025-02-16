package com.moim.feature.participantlist

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.route.DetailRoute

fun NavGraphBuilder.participantListNavigation(
    padding: PaddingValues,
    navigateToBack: () -> Unit,
) {
    composable<DetailRoute.ParticipantList>(
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) }
    ) {
        ParticipantListRoute(
            padding = padding,
            navigateToBack = navigateToBack
        )
    }
}

fun NavController.navigateToParticipantList(
    isMeeting: Boolean,
    isPlan: Boolean,
    id: String,
    navOptions: NavOptions? = null
) {
    this.navigate(
        DetailRoute.ParticipantList(
            isMeeting = isMeeting,
            isPlan = isPlan,
            id = id
        ), navOptions
    )
}