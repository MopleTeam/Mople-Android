package com.moim.feature.commentdetail

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.common.model.Comment
import com.moim.core.common.route.DetailRoute

fun NavGraphBuilder.commentDetailScreen(
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    navigateToImageViewer: (
        title: String,
        images: List<String>,
        position: Int,
        defaultImage: Int
    ) -> Unit
) {
    composable<DetailRoute.CommentDetail>(
        typeMap = DetailRoute.CommentDetail.typeMap,
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) },
    ) {
        CommentDetailRoute(
            padding = padding,
            navigateToBack = navigateToBack,
            navigateToImageViewer = navigateToImageViewer
        )
    }
}

fun NavController.navigateToCommentDetail(
    meetId: String,
    postId: String,
    comment: Comment,
    navOptions: NavOptions? = null
) {
    navigate(DetailRoute.CommentDetail(meetId, postId, comment), navOptions)
}