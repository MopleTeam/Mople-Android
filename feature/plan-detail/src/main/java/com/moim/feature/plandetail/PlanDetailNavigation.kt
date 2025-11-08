package com.moim.feature.plandetail

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.common.model.Comment
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.model.item.PlanItem
import com.moim.core.ui.route.DetailRoute

fun NavGraphBuilder.planDetailScreen(
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    navigateToMapDetail: (
        placeName: String,
        address: String,
        latitude: Double,
        longitude: Double
    ) -> Unit,
    navigateToParticipants: (ViewIdType) -> Unit,
    navigateToPlanWrite: (PlanItem) -> Unit,
    navigateToCommentDetail: (
        meetId: String,
        postId: String,
        comment: Comment
    ) -> Unit,
    navigateToReviewWrite: (
        id: String,
        isUpdated: Boolean
    ) -> Unit,
    navigateToImageViewer: (
        title: String,
        images: List<String>,
        position: Int,
        defaultImage: Int
    ) -> Unit
) {
    composable<DetailRoute.PlanDetail>(
        typeMap = DetailRoute.PlanDetail.typeMap,
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(0)) }
    ) {
        PlanDetailRoute(
            padding = padding,
            navigateToBack = navigateToBack,
            navigateToMapDetail = navigateToMapDetail,
            navigateToParticipants = navigateToParticipants,
            navigateToPlanWrite = navigateToPlanWrite,
            navigateToCommentDetail = navigateToCommentDetail,
            navigateToReviewWrite = navigateToReviewWrite,
            navigateToImageViewer = navigateToImageViewer
        )
    }
}

fun NavController.navigateToPlanDetail(
    viewIdType: ViewIdType,
    navOptions: NavOptions? = null
) {
    this.navigate(DetailRoute.PlanDetail(viewIdType), navOptions)
}