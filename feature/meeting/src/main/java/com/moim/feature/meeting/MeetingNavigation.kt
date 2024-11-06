package com.moim.feature.meeting

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.route.MainRoute

fun NavGraphBuilder.meetingScreen(
    padding: PaddingValues
) {
    composable<MainRoute.Meeting> {
        MeetingRoute(
            padding = padding
        )
    }
}

fun NavController.navigateToMeeting(navOptions: NavOptions? = null) {
    this.navigate(MainRoute.Meeting, navOptions)
}