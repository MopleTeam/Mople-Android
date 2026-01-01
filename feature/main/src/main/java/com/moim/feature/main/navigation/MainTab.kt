package com.moim.feature.main.navigation

import com.moim.core.designsystem.R
import com.moim.core.ui.route.MainRoute

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
        /**
         * 조건에 맞는 MainTab 찾기
         */
        fun find(predicate: (MainRoute) -> Boolean): MainTab? {
            return entries.find { predicate(it.route) }
        }

        /**
         * 조건에 맞는 Route가 있는지 확인
         */
        fun contains(predicate: (MainRoute) -> Boolean): Boolean {
            return entries.map { it.route }.any { predicate(it) }
        }

        /**
         * 모든 MainRoute 리스트 반환
         */
        val routes: List<MainRoute>
            get() = entries.map { it.route }
    }
}