package com.moim.feature.meetingdetail

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.model.Meeting
import com.moim.core.model.Plan
import com.moim.core.route.DetailRoute

fun NavGraphBuilder.meetingDetailScreen(
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    navigateToPlanWrite: (Plan) -> Unit,
    navigateToPlanDetail: (String, Boolean) -> Unit,
    navigateToMeetingSetting: (Meeting) -> Unit
) {
    composable<DetailRoute.MeetingDetail>(
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) }
    ) {
        MeetingDetailRoute(
            padding = padding,
            navigateToBack = navigateToBack,
            navigateToPlanWrite = navigateToPlanWrite,
            navigateToPlanDetail = navigateToPlanDetail,
            navigateToMeetingSetting = navigateToMeetingSetting
        )
    }
}

fun NavController.navigateToMeetingDetail(meetingId: String, navOptions: NavOptions? = null) {
    this.navigate(DetailRoute.MeetingDetail(meetingId), navOptions)
}