package com.moim.feature.meetingwrite

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.route.DetailRoute

fun NavGraphBuilder.meetingWriteScreen(
    padding: PaddingValues,
    navigateToBack: () -> Unit,
) {
    composable<DetailRoute.MeetingWrite>(
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) }
    ) {
        MeetingWriteRoute(
            padding = padding,
            navigateToBack = navigateToBack
        )
    }
}

fun NavController.navigateToMeetingWrite(
    writeId: String? = null,
    navOptions: NavOptions? = null
) {
    this.navigate(DetailRoute.MeetingWrite(writeId), navOptions)
}
