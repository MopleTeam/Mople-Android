package com.moim.feature.meetingsetting

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.model.Meeting
import com.moim.core.route.DetailRoute

fun NavGraphBuilder.meetingSettingScreen(
    padding: PaddingValues,
    navigateToBack: (Boolean) -> Unit,
    navigateToParticipants: (Boolean, Boolean, String) -> Unit,
    navigateToMeetingWrite: (Meeting) -> Unit,
) {
    composable<DetailRoute.MeetingSetting>(
        typeMap = DetailRoute.MeetingSetting.typeMap,
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) }
    ) {
        MeetingSettingRoute(
            padding = padding,
            navigateToBack = navigateToBack,
            navigateToParticipants = navigateToParticipants,
            navigateToMeetingWrite = navigateToMeetingWrite
        )
    }
}

fun NavController.navigateToMeetingSetting(
    meeting: Meeting,
    navOptions: NavOptions? = null
) {
    navigate(DetailRoute.MeetingSetting(meeting), navOptions)
}