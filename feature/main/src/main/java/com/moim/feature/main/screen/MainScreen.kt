package com.moim.feature.main.screen

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.moim.core.common.view.ObserveAsEvents
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.alarm.alarmScreen
import com.moim.feature.alarm.navigateToAlarm
import com.moim.feature.alarmsetting.alarmSettingScreen
import com.moim.feature.alarmsetting.navigateToAlarmSetting
import com.moim.feature.calendar.calendarScreen
import com.moim.feature.home.homeScreen
import com.moim.feature.imageviewer.imageViewerScreen
import com.moim.feature.imageviewer.navigateToImageViewer
import com.moim.feature.main.MainUiEvent
import com.moim.feature.main.MainViewModel
import com.moim.feature.main.navigation.MainNavController
import com.moim.feature.main.navigation.MainTab
import com.moim.feature.main.navigation.rememberMainNavController
import com.moim.feature.main.screen.ui.MainBottomBar
import com.moim.feature.mapdetail.mapDetailScreen
import com.moim.feature.mapdetail.navigateToMapDetail
import com.moim.feature.meeting.meetingScreen
import com.moim.feature.meetingdetail.meetingDetailScreen
import com.moim.feature.meetingdetail.navigateToMeetingDetail
import com.moim.feature.meetingsetting.meetingSettingScreen
import com.moim.feature.meetingsetting.navigateToMeetingSetting
import com.moim.feature.meetingwrite.meetingWriteScreen
import com.moim.feature.meetingwrite.navigateToMeetingWrite
import com.moim.feature.participantlist.navigateToParticipantList
import com.moim.feature.participantlist.participantListNavigation
import com.moim.feature.plandetail.navigateToPlanDetail
import com.moim.feature.plandetail.planDetailScreen
import com.moim.feature.planwrite.navigateToPlanWrite
import com.moim.feature.planwrite.planWriteScreen
import com.moim.feature.profile.profileScreen
import com.moim.feature.profileupdate.navigateToProfileUpdate
import com.moim.feature.profileupdate.profileUpdateScreen
import com.moim.feature.reviewwrite.navigateToReviewWrite
import com.moim.feature.reviewwrite.reviewWriteScreen
import com.moim.feature.webview.navigateToWebView
import com.moim.feature.webview.webViewScreen

@Composable
fun MainScreen(
    navigator: MainNavController = rememberMainNavController(),
    viewModel: MainViewModel,
    navigateToIntro: () -> Unit,
) {
    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is MainUiEvent.NavigateToPlanDetail -> navigator.navController.navigateToPlanDetail(event.planId, true)
            is MainUiEvent.NavigateToReviewDetail -> navigator.navController.navigateToPlanDetail(event.reviewId, false)
            is MainUiEvent.NavigateToMeetingDetail -> navigator.navController.navigateToMeetingDetail(event.meetingId)
        }
    }

    MoimScaffold(
        content = { innerPadding ->
            NavHost(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MoimTheme.colors.white),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                navController = navigator.navController,
                startDestination = navigator.startDestination,
            ) {
                homeScreen(
                    padding = innerPadding,
                    navigateToAlarm = navigator.navController::navigateToAlarm,
                    navigateToMeetingWrite = navigator.navController::navigateToMeetingWrite,
                    navigateToPlanWrite = navigator.navController::navigateToPlanWrite,
                    navigateToCalendar = { navigator.navigate(MainTab.Calendar) },
                    navigateToPlanDetail = navigator.navController::navigateToPlanDetail,
                )
                meetingScreen(
                    padding = innerPadding,
                    navigateToMeetingWrite = navigator.navController::navigateToMeetingWrite,
                    navigateToMeetingDetail = navigator.navController::navigateToMeetingDetail
                )
                meetingWriteScreen(
                    padding = innerPadding,
                    navigateToBack = navigator.navController::popBackStack
                )
                meetingDetailScreen(
                    padding = innerPadding,
                    navigateToBack = navigator.navController::popBackStack,
                    navigateToPlanWrite = navigator.navController::navigateToPlanWrite,
                    navigateToPlanDetail = navigator.navController::navigateToPlanDetail,
                    navigateToMeetingSetting = navigator.navController::navigateToMeetingSetting,
                    navigateToImageViewer = navigator.navController::navigateToImageViewer
                )
                meetingSettingScreen(
                    padding = innerPadding,
                    navigateToBack = {
                        if (it) navigator.navController.popBackStack()
                        navigator.navController.popBackStack()
                    },
                    navigateToParticipants = navigator.navController::navigateToParticipantList,
                    navigateToMeetingWrite = navigator.navController::navigateToMeetingWrite
                )
                reviewWriteScreen(
                    padding = innerPadding,
                    navigateToBack = navigator.navController::popBackStack,
                    navigateToParticipants = navigator.navController::navigateToParticipantList,
                )
                planWriteScreen(
                    padding = innerPadding,
                    navigateToBack = navigator.navController::popBackStack
                )
                planDetailScreen(
                    padding = innerPadding,
                    navigateToBack = navigator.navController::popBackStack,
                    navigateToMapDetail = navigator.navController::navigateToMapDetail,
                    navigateToParticipants = navigator.navController::navigateToParticipantList,
                    navigateToPlanWrite = navigator.navController::navigateToPlanWrite,
                    navigateToReviewWrite = navigator.navController::navigateToReviewWrite,
                    navigateToImageViewer = navigator.navController::navigateToImageViewer
                )
                mapDetailScreen(
                    innerPadding = innerPadding,
                    navigateToBack = navigator.navController::popBackStack
                )
                participantListNavigation(
                    padding = innerPadding,
                    navigateToBack = navigator.navController::popBackStack,
                    navigateToImageViewer = navigator.navController::navigateToImageViewer
                )
                calendarScreen(
                    padding = innerPadding,
                    navigateToPlanDetail = navigator.navController::navigateToPlanDetail
                )
                profileScreen(
                    padding = innerPadding,
                    navigateToProfileUpdate = navigator.navController::navigateToProfileUpdate,
                    navigateToAlarmSetting = navigator.navController::navigateToAlarmSetting,
                    navigateToPrivacyPolicy = navigator.navController::navigateToWebView,
                    navigateToIntro = navigateToIntro
                )
                profileUpdateScreen(
                    padding = innerPadding,
                    navigateToBack = navigator.navController::popBackStack
                )
                alarmScreen(
                    padding = innerPadding,
                    navigateToMeetingDetail = navigator.navController::navigateToMeetingDetail,
                    navigateToPlanDetail = navigator.navController::navigateToPlanDetail,
                    navigateToBack = navigator.navController::popBackStack,
                )
                alarmSettingScreen(
                    padding = innerPadding,
                    navigateToBack = navigator.navController::popBackStack
                )
                webViewScreen(
                    padding = innerPadding,
                    navigateToBack = navigator.navController::popBackStack
                )
                imageViewerScreen(
                    padding = innerPadding,
                    navigateToBack = navigator.navController::popBackStack
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
