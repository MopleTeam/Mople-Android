package com.moim.feature.main.screen

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.theme.color_FFFFFF
import com.moim.feature.calendar.calendarScreen
import com.moim.feature.home.homeScreen
import com.moim.feature.main.navigation.MainNavController
import com.moim.feature.main.navigation.MainTab
import com.moim.feature.main.navigation.rememberMainNavController
import com.moim.feature.main.screen.ui.MainBottomBar
import com.moim.feature.meeting.meetingScreen
import com.moim.feature.profile.profileScreen

@Composable
fun MainScreen(
    navigator: MainNavController = rememberMainNavController(),
) {
    MoimScaffold(
        content = { innerPadding ->
            NavHost(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color_FFFFFF),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                navController = navigator.navController,
                startDestination = navigator.startDestination,
            ) {
                homeScreen(
                    padding = innerPadding,
                    navigateToAlarm = {},
                    navigateToWriteGroup = {},
                    navigateToWriteMeeting = {},
                    navigateToCalendar = { navigator.navigate(MainTab.Calendar) },
                    navigateToMeetingDetail = {},
                )
                meetingScreen(
                    padding = innerPadding
                )
                calendarScreen(
                    padding = innerPadding
                )
                profileScreen(
                    padding = innerPadding
                )
            }
        },
        bottomBar = {
            MainBottomBar(
                isVisible = navigator.shouldShowBottomBar(),
                tabs = MainTab.entries,
                currentTab = navigator.currentTab,
                onTabSelected = navigator::navigate,
            )
        }
    )
}
