package com.moim.feature.main.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.view.ObserveAsEvents
import com.moim.feature.main.MainUiEvent
import com.moim.feature.main.MainViewModel
import com.moim.feature.main.navigation.MainNavController
import com.moim.feature.main.navigation.MainTab
import com.moim.feature.main.navigation.alarmScreenEntry
import com.moim.feature.main.navigation.alarmSettingScreenEntry
import com.moim.feature.main.navigation.calendarScreenEntry
import com.moim.feature.main.navigation.commentDetailScreenEntry
import com.moim.feature.main.navigation.homeScreenEntry
import com.moim.feature.main.navigation.imageViewerScreenEntry
import com.moim.feature.main.navigation.mapDetailScreenEntry
import com.moim.feature.main.navigation.meetingDetailScreenEntry
import com.moim.feature.main.navigation.meetingScreenEntry
import com.moim.feature.main.navigation.meetingSettingScreenEntry
import com.moim.feature.main.navigation.meetingWriteScreenEntry
import com.moim.feature.main.navigation.participantListScreenEntry
import com.moim.feature.main.navigation.planDetailScreenEntry
import com.moim.feature.main.navigation.planWriteScreenEntry
import com.moim.feature.main.navigation.profileScreenEntry
import com.moim.feature.main.navigation.profileUpdateScreenEntry
import com.moim.feature.main.navigation.rememberMainNavController
import com.moim.feature.main.navigation.reviewWriteScreenEntry
import com.moim.feature.main.navigation.toEntries
import com.moim.feature.main.navigation.webViewScreenEntry
import com.moim.feature.main.screen.ui.MainBottomBar

private const val NAV_ANIMATION_DELAY = 500

@Composable
fun MainScreen(
    mainNavController: MainNavController = rememberMainNavController(),
    viewModel: MainViewModel,
    navigateToIntro: () -> Unit,
) {
    val navigator = mainNavController.navigator
    val navigationState = mainNavController.navigationState

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is MainUiEvent.NavigateToPlanDetail -> {
                navigator.navigateToPlanDetail(event.viewIdType)
            }

            is MainUiEvent.NavigateToMeetingDetail -> {
                navigator.navigateToMeetingDetail(event.meetingId)
            }
        }
    }

    MoimScaffold(
        content = { innerPadding ->
            val entryProvider =
                entryProvider {
                    // Main Tabs
                    homeScreenEntry(navigator, innerPadding)
                    meetingScreenEntry(navigator, innerPadding)
                    calendarScreenEntry(navigator, innerPadding)
                    profileScreenEntry(navigator, innerPadding, navigateToIntro)

                    // Detail Screens
                    meetingDetailScreenEntry(navigator, innerPadding)
                    meetingWriteScreenEntry(navigator, innerPadding)
                    meetingSettingScreenEntry(navigator, innerPadding)
                    mapDetailScreenEntry(navigator, innerPadding)
                    planDetailScreenEntry(navigator, innerPadding)
                    commentDetailScreenEntry(navigator, innerPadding)
                    planWriteScreenEntry(navigator, innerPadding)
                    reviewWriteScreenEntry(navigator, innerPadding)
                    participantListScreenEntry(navigator, innerPadding)
                    imageViewerScreenEntry(navigator, innerPadding)
                    profileUpdateScreenEntry(navigator, innerPadding)
                    alarmScreenEntry(navigator, innerPadding)
                    alarmSettingScreenEntry(navigator, innerPadding)
                    webViewScreenEntry(navigator, innerPadding)
                }

            NavDisplay(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(MoimTheme.colors.white),
                entries = navigationState.toEntries(entryProvider),
                onBack = { navigator.goBack() },
                transitionSpec = {
                    fadeIn(
                        animationSpec = tween(NAV_ANIMATION_DELAY),
                    ) togetherWith
                        fadeOut(
                            animationSpec = tween(NAV_ANIMATION_DELAY),
                        )
                },
                popTransitionSpec = {
                    fadeIn(
                        animationSpec = tween(NAV_ANIMATION_DELAY),
                    ) togetherWith
                        fadeOut(
                            animationSpec = tween(NAV_ANIMATION_DELAY),
                        )
                },
                predictivePopTransitionSpec = {
                    // Fade in for predictive back gesture
                    fadeIn(
                        animationSpec = tween(NAV_ANIMATION_DELAY),
                    ) togetherWith
                        fadeOut(
                            animationSpec = tween(NAV_ANIMATION_DELAY),
                        )
                },
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = navigationState.shouldShowBottomBar,
                enter = EnterTransition.None,
                exit = ExitTransition.None,
            ) {
                MainBottomBar(
                    tabs = MainTab.entries.toList(),
                    currentTab = mainNavController.currentTab,
                    onTabSelected = mainNavController::navigate,
                )
            }
        },
    )
}
