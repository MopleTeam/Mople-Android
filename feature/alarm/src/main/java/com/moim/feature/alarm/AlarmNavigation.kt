package com.moim.feature.alarm

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.route.DetailRoute


fun NavGraphBuilder.alarmScreen(
    padding: PaddingValues,
    navigateToMeetingDetail: (String) -> Unit,
    navigateToPlanDetail: (String, Boolean) -> Unit,
    navigateToBack: () -> Unit,
) {
    composable<DetailRoute.Alarm>(
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) }
    ) {
        AlarmRoute(
            padding = padding,
            navigateToMeetingDetail = navigateToMeetingDetail,
            navigateToPlanDetail = navigateToPlanDetail,
            navigateToBack = navigateToBack,
        )
    }
}

fun NavController.navigateToAlarm(navOptions: NavOptions? = null) {
    this.navigate(DetailRoute.Alarm, navOptions)
}