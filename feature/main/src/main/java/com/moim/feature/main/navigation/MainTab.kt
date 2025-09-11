package com.moim.feature.main.navigation

import androidx.compose.runtime.Composable
import com.moim.core.designsystem.R
import com.moim.core.common.route.MainRoute
import com.moim.core.common.route.Route

enum class MainTab(
    val iconResId: Int,
    val contentDescription: String,
    val label: String,
    val route: MainRoute,
) {
    Home(
        iconResId = R.drawable.ic_menu_home,
        contentDescription = "Home",
        label = "홈",
        route = MainRoute.Home
    ),
    Meeting(
        iconResId = R.drawable.ic_menu_meeting,
        contentDescription = "Meeting",
        label = "모임",
        route = MainRoute.Meeting
    ),
    Calendar(
        iconResId = R.drawable.ic_menu_calendar,
        contentDescription = "Calendar",
        label = "일정관리",
        route = MainRoute.Calendar
    ),
    Profile(
        iconResId = R.drawable.ic_menu_profile,
        contentDescription = "Profile",
        label = "프로필",
        route = MainRoute.Profile
    );

    companion object {
        @Composable
        fun find(predicate: @Composable (MainRoute) -> Boolean): MainTab? {
            return entries.find { predicate(it.route) }
        }

        @Composable
        fun contains(predicate: @Composable (Route) -> Boolean): Boolean {
            return entries.map { it.route }.any { predicate(it) }
        }
    }
}