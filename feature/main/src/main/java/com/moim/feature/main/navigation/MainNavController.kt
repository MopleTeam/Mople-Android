package com.moim.feature.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.moim.feature.calendar.navigateToCalendar
import com.moim.feature.home.navigateToHome
import com.moim.feature.meeting.navigateToMeeting
import com.moim.feature.profile.navigateToProfile

class MainNavController(
    val navController: NavHostController,
) {
    private val currentDestination: NavDestination?
        @Composable
        get() = navController.currentBackStackEntryAsState().value?.destination

    val currentTab: MainTab?
        @Composable
        get() = MainTab.find { tab -> currentDestination?.hasRoute(tab::class) == true }

    val startDestination = MainTab.Home.route

    fun navigate(tab: MainTab) {
        val navOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }

        when (tab) {
            MainTab.Home -> navController.navigateToHome(navOptions)
            MainTab.Meeting -> navController.navigateToMeeting(navOptions)
            MainTab.Calendar -> navController.navigateToCalendar(navOptions)
            MainTab.Profile -> navController.navigateToProfile(navOptions)
        }
    }

    @Composable
    fun shouldShowBottomBar() = MainTab.contains {
        currentDestination?.hasRoute(it::class) == true
    }

    private fun popBackStack() {
        navController.popBackStack()
    }
}

@Composable
fun rememberMainNavController(
    navController: NavHostController = rememberNavController(),
): MainNavController {
    return remember(navController) { MainNavController(navController) }
}