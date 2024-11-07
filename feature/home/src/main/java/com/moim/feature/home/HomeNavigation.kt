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
    navigateToWriteMeeting: () -> Unit = {},
    navigateToWritePlan: () -> Unit = {},
    navigateToCalendar: () -> Unit = {},
    navigateToMeetingDetail: (String) -> Unit = {},
) {
    composable<MainRoute.Home> {
        HomeRoute(
            padding = padding,
            navigateToAlarm = navigateToAlarm,
            navigateToWriteMeeting = navigateToWriteMeeting,
            navigateToWritePlan = navigateToWritePlan,
            navigateToCalendar = navigateToCalendar,
            navigateToMeetingDetail = navigateToMeetingDetail,
        )
    }
}

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(MainRoute.Home, navOptions)
}