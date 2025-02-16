package com.moim.feature.alarmsetting

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.route.DetailRoute

fun NavGraphBuilder.alarmSettingScreen(
    padding: PaddingValues,
    navigateToBack: () -> Unit
) {
    composable<DetailRoute.AlarmSetting>(
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) }
    ) {
        AlarmSettingRoute(
            padding = padding,
            navigateToBack = navigateToBack
        )
    }
}

fun NavController.navigateToAlarmSetting(navOptions: NavOptions? = null) {
    this.navigate(DetailRoute.AlarmSetting, navOptions)
}