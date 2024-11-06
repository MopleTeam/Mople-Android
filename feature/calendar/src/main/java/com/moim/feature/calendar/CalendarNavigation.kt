package com.moim.feature.calendar

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.route.MainRoute


fun NavGraphBuilder.calendarScreen(
    padding: PaddingValues
) {
    composable<MainRoute.Calendar> {
        CalendarRoute(
            padding = padding
        )
    }
}

fun NavController.navigateToCalendar(navOptions: NavOptions? = null) {
    this.navigate(MainRoute.Calendar, navOptions)
}