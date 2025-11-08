package com.moim.feature.participantlist

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.route.DetailRoute

fun NavGraphBuilder.participantListNavigation(
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    navigateToImageViewer: (title: String, images: List<String>, position: Int, defaultImage: Int) -> Unit,
) {
    composable<DetailRoute.ParticipantList>(
        typeMap = DetailRoute.ParticipantList.typeMap,
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) }
    ) {
        ParticipantListRoute(
            padding = padding,
            navigateToBack = navigateToBack,
            navigateToImageViewer = navigateToImageViewer
        )
    }
}

fun NavController.navigateToParticipantList(
    viewIdType: ViewIdType,
    navOptions: NavOptions? = null
) {
    this.navigate(DetailRoute.ParticipantList(viewIdType), navOptions)
}