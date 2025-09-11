package com.moim.feature.meetingdetail

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.common.model.Meeting
import com.moim.core.common.model.item.PlanItem
import com.moim.core.common.route.DetailRoute

fun NavGraphBuilder.meetingDetailScreen(
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    navigateToPlanWrite: (PlanItem) -> Unit,
    navigateToPlanDetail: (String, Boolean) -> Unit,
    navigateToMeetingSetting: (Meeting) -> Unit,
    navigateToImageViewer: (title: String, images: List<String>, position: Int, defaultImage: Int) -> Unit,
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
            navigateToMeetingSetting = navigateToMeetingSetting,
            navigateToImageViewer = navigateToImageViewer
        )
    }
}

fun NavController.navigateToMeetingDetail(meetingId: String, navOptions: NavOptions? = null) {
    this.navigate(DetailRoute.MeetingDetail(meetingId), navOptions)
}