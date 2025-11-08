package com.moim.feature.plandetail

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.common.model.Comment
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.model.item.PlanItem
import com.moim.core.ui.util.toValidUrl
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.common.PagingErrorScreen
import com.moim.core.designsystem.common.PagingLoadingScreen
import com.moim.core.designsystem.component.MoimAlertDialog
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.moimButtomColors
import com.moim.core.ui.view.ObserveAsEvents
import com.moim.core.ui.view.PAGING_ERROR
import com.moim.core.ui.view.PAGING_LOADING
import com.moim.core.ui.view.isAppendError
import com.moim.core.ui.view.isAppendLoading
import com.moim.core.ui.view.isError
import com.moim.core.ui.view.isLoading
import com.moim.core.ui.view.showToast
import com.moim.feature.plandetail.ui.PlanDetailBottomBar
import com.moim.feature.plandetail.ui.PlanDetailCommentEditDialog
import com.moim.feature.plandetail.ui.PlanDetailCommentHeader
import com.moim.feature.plandetail.ui.PlanDetailCommentItem
import com.moim.feature.plandetail.ui.PlanDetailCommentReportDialog
import com.moim.feature.plandetail.ui.PlanDetailContent
import com.moim.feature.plandetail.ui.PlanDetailEditDialog
import com.moim.feature.plandetail.ui.PlanDetailMentionDialog
import com.moim.feature.plandetail.ui.PlanDetailReportDialog
import com.moim.feature.plandetail.ui.PlanDetailReviewImages
import com.moim.feature.plandetail.ui.PlanDetailTopAppbar

internal typealias OnPlanDetailUiAction = (PlanDetailUiAction) -> Unit

@Composable
fun PlanDetailRoute(
    viewModel: PlanDetailViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    navigateToMapDetail: (
        placeName: String,
        address: String,
        latitude: Double,
        longitude: Double
    ) -> Unit,
    navigateToParticipants: (ViewIdType) -> Unit,
    navigateToPlanWrite: (
        planItem: PlanItem
    ) -> Unit,
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
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val planDetailUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.white)

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is PlanDetailUiEvent.NavigateToBack -> navigateToBack()
            is PlanDetailUiEvent.NavigateToParticipants -> navigateToParticipants(event.viewIdType)
            is PlanDetailUiEvent.NavigateToPlanWrite -> navigateToPlanWrite(event.planItem)
            is PlanDetailUiEvent.NavigateToReviewWrite -> navigateToReviewWrite(event.postId, true)
            is PlanDetailUiEvent.NavigateToCommentDetail -> navigateToCommentDetail(event.meetId, event.postId, event.comment)
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
    val comments = uiState.comments?.collectAsLazyPagingItems(LocalLifecycleOwner.current.lifecycleScope.coroutineContext)

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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
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
                            commentCount = uiState.planItem.commentCount
                        )
                    }

                    if (comments != null) {
                        items(
                            count = comments.itemCount,
                            key = comments.itemKey(),
                            contentType = comments.itemContentType(),
                        ) { index ->
                            val comment = comments[index] ?: return@items
                            PlanDetailCommentItem(
                                modifier = Modifier.animateItem(),
                                userId = uiState.user.userId,
                                comment = comment,
                                onUiAction = onUiAction
                            )
                        }

                        if (comments.loadState.isAppendLoading()) {
                            item(key = PAGING_LOADING) {
                                PagingLoadingScreen(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .background(MoimTheme.colors.white)
                                            .animateItem(),
                                )
                            }
                        }

                        if (comments.loadState.isAppendError()) {
                            item(key = PAGING_ERROR) {
                                PagingErrorScreen(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .background(MoimTheme.colors.white)
                                            .animateItem(),
                                    onClickRetry = comments::retry,
                                )
                            }
                        }


                        item {
                            AnimatedVisibility(
                                enter = fadeIn(),
                                exit = fadeOut(),
                                visible = comments.loadState.isLoading(),
                            ) {
                                PagingLoadingScreen()
                            }
                        }

                        item {
                            AnimatedVisibility(
                                modifier = Modifier.fillMaxWidth(),
                                enter = fadeIn(),
                                exit = fadeOut(),
                                visible = comments.loadState.isError()
                            ) {
                                PagingErrorScreen(
                                    modifier = modifier,
                                    onClickRetry = comments::refresh,
                                )
                            }
                        }
                    }
                }

                if (uiState.isShowMentionDialog) {
                    PlanDetailMentionDialog(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        userList = uiState.searchMentions,
                        onUiAction = onUiAction
                    )
                }
            }
        },
        bottomBar = {
            PlanDetailBottomBar(
                updateComment = uiState.selectedUpdateComment,
                commentState = uiState.commentState,
                selectedMentions = uiState.selectedMentions,
                onUiAction = onUiAction
            )
        }
    )

    if (uiState.isShowApplyCancelDialog) {
        val dismissAction = PlanDetailUiAction.OnShowPlanApplyCancelDialog(false)

        MoimAlertDialog(
            title = stringResource(R.string.meeting_detail_plan_cancel),
            positiveButtonColors = moimButtomColors().copy(containerColor = MoimTheme.colors.secondary),
            onDismiss = { onUiAction(dismissAction) },
            onClickNegative = { onUiAction(dismissAction) },
            onClickPositive = { onUiAction(PlanDetailUiAction.OnClickPlanApply(false)) }
        )
    }

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