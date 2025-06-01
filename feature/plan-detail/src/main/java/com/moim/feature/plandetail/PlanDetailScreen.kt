package com.moim.feature.plandetail

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.common.util.toValidUrl
import com.moim.core.common.view.ObserveAsEvents
import com.moim.core.common.view.showToast
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.model.item.PlanItem
import com.moim.feature.plandetail.ui.PlanDetailBottomBar
import com.moim.feature.plandetail.ui.PlanDetailCommentEditDialog
import com.moim.feature.plandetail.ui.PlanDetailCommentHeader
import com.moim.feature.plandetail.ui.PlanDetailCommentItem
import com.moim.feature.plandetail.ui.PlanDetailCommentReportDialog
import com.moim.feature.plandetail.ui.PlanDetailContent
import com.moim.feature.plandetail.ui.PlanDetailEditDialog
import com.moim.feature.plandetail.ui.PlanDetailReportDialog
import com.moim.feature.plandetail.ui.PlanDetailReviewImages
import com.moim.feature.plandetail.ui.PlanDetailTopAppbar

internal typealias OnPlanDetailUiAction = (PlanDetailUiAction) -> Unit

@Composable
fun PlanDetailRoute(
    viewModel: PlanDetailViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    navigateToMapDetail: (placeName: String, address: String, latitude: Double, longitude: Double) -> Unit,
    navigateToParticipants: (isMeeting: Boolean, isPlan: Boolean, id: String) -> Unit,
    navigateToPlanWrite: (PlanItem) -> Unit,
    navigateToReviewWrite: (id: String, isUpdated: Boolean) -> Unit,
    navigateToImageViewer: (title: String, images: List<String>, position: Int, defaultImage: Int) -> Unit
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val planDetailUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.white)

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is PlanDetailUiEvent.NavigateToBack -> navigateToBack()
            is PlanDetailUiEvent.NavigateToParticipants -> navigateToParticipants(false, event.isPlan, event.postId)
            is PlanDetailUiEvent.NavigateToPlanWrite -> navigateToPlanWrite(event.planItem)
            is PlanDetailUiEvent.NavigateToReviewWrite -> navigateToReviewWrite(event.postId, true)
            is PlanDetailUiEvent.NavigateToMapDetail -> navigateToMapDetail(event.placeName, event.address, event.latitude, event.longitude)
            is PlanDetailUiEvent.NavigateToImageViewerForReview -> navigateToImageViewer(context.getString(R.string.plan_detail_image), event.images, event.position, R.drawable.ic_empty_user_logo)
            is PlanDetailUiEvent.NavigateToImageViewerForUser -> navigateToImageViewer(event.userName, listOf(event.image), 0, R.drawable.ic_empty_user_logo)
            is PlanDetailUiEvent.NavigateToWebBrowser -> {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, event.webLink.toValidUrl()))
                } catch (e: Exception) {
                    showToast(context, context.getString(R.string.common_error_open_browser))
                }
            }

            is PlanDetailUiEvent.ShowToastMessage -> showToast(context, event.message)
        }
    }

    when (val uiState = planDetailUiState) {
        is PlanDetailUiState.Loading -> LoadingScreen(modifier)

        is PlanDetailUiState.Success -> PlanDetailScreen(
            modifier = modifier,
            uiState = uiState,
            isLoading = isLoading,
            onUiAction = viewModel::onUiAction
        )

        is PlanDetailUiState.Error -> ErrorScreen(
            modifier = modifier,
            onClickRefresh = { viewModel.onUiAction(PlanDetailUiAction.OnClickRefresh) }
        )
    }
}

@Composable
fun PlanDetailScreen(
    modifier: Modifier = Modifier,
    uiState: PlanDetailUiState.Success,
    isLoading: Boolean,
    onUiAction: OnPlanDetailUiAction
) {
    val screenName = if (uiState.planItem.isPlanAtBefore) "plan_detail" else "review_detail"
    TrackScreenViewEvent(screenName = screenName)
    MoimScaffold(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            PlanDetailTopAppbar(
                isMyPlan = uiState.user.userId == uiState.planItem.userId,
                onUiAction = onUiAction
            )
        },
        content = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                item {
                    PlanDetailContent(
                        planItem = uiState.planItem,
                        isShowApplyButton = uiState.isShowApplyButton,
                        onUiAction = onUiAction
                    )
                }

                item {
                    PlanDetailSpacer()
                }

                item {
                    PlanDetailReviewImages(
                        images = uiState.planItem.reviewImages,
                        onUiAction = onUiAction
                    )
                }

                item {
                    PlanDetailSpacer()
                }

                item {
                    PlanDetailCommentHeader(
                        commentCount = uiState.comments.size
                    )
                }

                items(
                    items = uiState.comments,
                    key = { comment -> comment.comment.commentId }
                ) { comment ->
                    PlanDetailCommentItem(
                        userId = uiState.user.userId,
                        comment = comment,
                        onUiAction = onUiAction
                    )
                }
            }
        },
        bottomBar = {
            PlanDetailBottomBar(
                updateComment = uiState.selectedUpdateComment,
                onUiAction = onUiAction
            )
        }
    )

    if (uiState.isShowPlanEditDialog) {
        PlanDetailEditDialog(
            onUiAction = onUiAction
        )
    }

    if (uiState.isShowPlanReportDialog) {
        PlanDetailReportDialog(
            onUiAction = onUiAction
        )
    }

    if (uiState.isShowCommentEditDialog && uiState.selectedComment != null) {
        PlanDetailCommentEditDialog(
            comment = uiState.selectedComment,
            onUiAction = onUiAction
        )
    }

    if (uiState.isShowCommentReportDialog && uiState.selectedComment != null) {
        PlanDetailCommentReportDialog(
            comment = uiState.selectedComment,
            onUiAction = onUiAction
        )
    }

    LoadingDialog(isLoading)
}

@Composable
private fun PlanDetailSpacer() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(MoimTheme.colors.stroke)
    )
}