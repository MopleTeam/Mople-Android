package com.moim.feature.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.route.MainRoute

fun NavGraphBuilder.homeScreen(
    padding: PaddingValues,
    navigateToAlarm: () -> Unit = {},
    navigateToMeetingWrite: () -> Unit = {},
    navigateToPlanWrite: () -> Unit = {},
    navigateToCalendar: () -> Unit = {},
    navigateToPlanDetail: (String, Boolean) -> Unit,
) {
    composable<MainRoute.Home> {
        HomeRoute(
            padding = padding,
            navigateToAlarm = navigateToAlarm,
            navigateToMeetingWrite = navigateToMeetingWrite,
            navigateToPlanWrite = navigateToPlanWrite,
            navigateToCalendar = navigateToCalendar,
            navigateToPlanDetail = navigateToPlanDetail,
        )
    }
}

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(MainRoute.Home, navOptions)
}