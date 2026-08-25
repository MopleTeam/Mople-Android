package com.moim.feature.plandetail.model

import com.moim.core.common.model.Comment
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.model.item.PlanItem
import com.moim.core.ui.view.ToastMessage

sealed interface PlanDetailSideEffect {
    data object NavigateToBack : PlanDetailSideEffect

    data class NavigateToParticipants(
        val viewIdType: ViewIdType,
    ) : PlanDetailSideEffect

    data class NavigateToPlanWrite(
        val planItem: PlanItem,
    ) : PlanDetailSideEffect

    data class NavigateToReviewWrite(
        val postId: String,
    ) : PlanDetailSideEffect

    data class NavigateToMapDetail(
        val placeName: String,
        val address: String,
        val latitude: Double,
        val longitude: Double,
    ) : PlanDetailSideEffect

    data class NavigateToImageViewerForReview(
        val images: List<String>,
        val position: Int,
    ) : PlanDetailSideEffect

    data class NavigateToImageViewerForUser(
        val image: String,
        val userName: String,
    ) : PlanDetailSideEffect

    data class NavigateToWebBrowser(
        val webLink: String,
    ) : PlanDetailSideEffect

    data class NavigateToCommentDetail(
        val meetId: String,
        val postId: String,
        val comment: Comment,
    ) : PlanDetailSideEffect

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : PlanDetailSideEffect
}
