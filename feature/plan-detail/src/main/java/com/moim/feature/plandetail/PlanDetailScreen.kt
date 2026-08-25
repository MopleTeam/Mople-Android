package com.moim.feature.plandetail

import android.content.Intent
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.common.model.Comment
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.model.item.PlanItem
import com.moim.core.common.result.data
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.common.NotFoundErrorScreen
import com.moim.core.designsystem.common.PagingErrorScreen
import com.moim.core.designsystem.common.PagingLoadingScreen
import com.moim.core.designsystem.component.MoimAlertDialog
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.moimButtomColors
import com.moim.core.ui.util.toValidUrl
import com.moim.core.ui.view.FadeAnimatedVisibility
import com.moim.core.ui.view.PaginationEffect
import com.moim.core.ui.view.showToast
import com.moim.feature.plandetail.model.PlanDetailIntent
import com.moim.feature.plandetail.model.PlanDetailSideEffect
import com.moim.feature.plandetail.model.PlanDetailState
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

internal typealias OnPlanDetailIntent = (PlanDetailIntent) -> Unit

@Composable
fun PlanDetailRoute(
    viewModel: PlanDetailViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    navigateToMapDetail: (
        placeName: String,
        address: String,
        latitude: Double,
        longitude: Double,
    ) -> Unit,
    navigateToParticipants: (ViewIdType) -> Unit,
    navigateToPlanWrite: (
        planItem: PlanItem,
    ) -> Unit,
    navigateToCommentDetail: (
        meetId: String,
        postId: String,
        comment: Comment,
    ) -> Unit,
    navigateToReviewWrite: (
        id: String,
        isUpdated: Boolean,
    ) -> Unit,
    navigateToImageViewer: (
        title: String,
        images: List<String>,
        position: Int,
        defaultImage: Int,
    ) -> Unit,
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val uiState by viewModel.collectAsState()
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.bg.primary)

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is PlanDetailSideEffect.NavigateToBack -> {
                navigateToBack()
            }

            is PlanDetailSideEffect.NavigateToParticipants -> {
                navigateToParticipants(sideEffect.viewIdType)
            }

            is PlanDetailSideEffect.NavigateToPlanWrite -> {
                navigateToPlanWrite(sideEffect.planItem)
            }

            is PlanDetailSideEffect.NavigateToReviewWrite -> {
                navigateToReviewWrite(sideEffect.postId, true)
            }

            is PlanDetailSideEffect.NavigateToCommentDetail -> {
                navigateToCommentDetail(sideEffect.meetId, sideEffect.postId, sideEffect.comment)
            }

            is PlanDetailSideEffect.NavigateToMapDetail -> {
                navigateToMapDetail(sideEffect.placeName, sideEffect.address, sideEffect.latitude, sideEffect.longitude)
            }

            is PlanDetailSideEffect.NavigateToImageViewerForReview -> {
                navigateToImageViewer(
                    context.getString(R.string.plan_detail_image),
                    sideEffect.images,
                    sideEffect.position,
                    R.drawable.ic_empty_user_logo,
                )
            }

            is PlanDetailSideEffect.NavigateToImageViewerForUser -> {
                navigateToImageViewer(sideEffect.userName, listOf(sideEffect.image), 0, R.drawable.ic_empty_user_logo)
            }

            is PlanDetailSideEffect.NavigateToWebBrowser -> {
                runCatching {
                    context.startActivity(Intent(Intent.ACTION_VIEW, sideEffect.webLink.toValidUrl()))
                }.onFailure {
                    showToast(context, context.getString(R.string.common_error_open_browser))
                }
            }

            is PlanDetailSideEffect.ShowToastMessage -> {
                showToast(context, sideEffect.message)
            }
        }
    }

    when {
        uiState.isLoading -> {
            LoadingScreen(modifier)
        }

        uiState.isSuccess -> {
            PlanDetailScreen(
                modifier = modifier,
                uiState = uiState,
                isLoading = isLoading,
                onIntent = viewModel::onIntent,
            )
        }

        uiState.isNotFoundError -> {
            NotFoundErrorScreen(
                modifier = modifier,
                onClickBack = { viewModel.onIntent(PlanDetailIntent.BackClick) },
            )
        }

        uiState.isError -> {
            ErrorScreen(
                modifier = modifier,
                onClickRefresh = { viewModel.onIntent(PlanDetailIntent.RefreshClick) },
            )
        }
    }
}

@Composable
fun PlanDetailScreen(
    modifier: Modifier = Modifier,
    uiState: PlanDetailState,
    isLoading: Boolean,
    onIntent: OnPlanDetailIntent,
) {
    val planItem = uiState.planItem.data ?: return
    val screenName = if (planItem.isPlanAtBefore) "plan_detail" else "review_detail"
    val comments = uiState.comments
    val pagingInfo = uiState.commentsPagingInfo
    val listState = rememberLazyListState()

    PaginationEffect(
        listState = listState,
        threshold = 3,
        enabled = !pagingInfo.isLast && !pagingInfo.isErrorFooter,
        onNext = { onIntent(PlanDetailIntent.NextCommentsPageLoad) },
    )

    TrackScreenViewEvent(screenName = screenName)
    MoimScaffold(
        modifier =
            modifier
                .fillMaxSize()
                .imePadding(),
        topBar = {
            PlanDetailTopAppbar(
                isMyPlan = uiState.user.userId == planItem.userId,
                onIntent = onIntent,
            )
        },
        content = {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(it),
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                ) {
                    item {
                        PlanDetailContent(
                            isMyPlan = uiState.user.userId == planItem.userId,
                            planItem = planItem,
                            isShowApplyButton = uiState.isShowApplyButton,
                            onIntent = onIntent,
                        )
                    }

                    item {
                        PlanDetailSpacer()
                    }

                    item {
                        PlanDetailReviewImages(
                            images = planItem.reviewImages,
                            onIntent = onIntent,
                        )
                    }

                    item {
                        PlanDetailSpacer()
                    }

                    item {
                        PlanDetailCommentHeader(
                            commentCount = planItem.commentCount,
                        )
                    }

                    items(
                        items = comments,
                        key = { it.comment.commentId },
                    ) { commentUiModel ->
                        PlanDetailCommentItem(
                            modifier = Modifier.animateItem(),
                            userId = uiState.user.userId,
                            comment = commentUiModel,
                            onIntent = onIntent,
                        )
                    }

                    item {
                        FadeAnimatedVisibility(pagingInfo.isLoading || pagingInfo.isLoadingFooter) {
                            PagingLoadingScreen(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .background(MoimTheme.colors.bg.primary),
                            )
                        }
                    }

                    item {
                        FadeAnimatedVisibility(pagingInfo.isError || pagingInfo.isErrorFooter) {
                            PagingErrorScreen(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .background(MoimTheme.colors.bg.primary),
                                onClickRetry = { onIntent(PlanDetailIntent.NextCommentsPageLoad) },
                            )
                        }
                    }
                }

                if (uiState.isShowMentionDialog) {
                    PlanDetailMentionDialog(
                        modifier =
                            Modifier
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                        userList = uiState.searchMentions,
                        onIntent = onIntent,
                    )
                }
            }
        },
        bottomBar = {
            PlanDetailBottomBar(
                updateComment = uiState.selectedUpdateComment,
                commentState = uiState.commentState,
                selectedMentions = uiState.selectedMentions,
                onIntent = onIntent,
            )
        },
    )

    if (uiState.isShowApplyCancelDialog) {
        val dismissIntent = PlanDetailIntent.PlanApplyCancelDialogShow(false)

        MoimAlertDialog(
            title = stringResource(R.string.meeting_detail_plan_cancel),
            positiveButtonColors = moimButtomColors().copy(containerColor = MoimTheme.colors.secondary),
            onDismiss = { onIntent(dismissIntent) },
            onClickNegative = { onIntent(dismissIntent) },
            onClickPositive = { onIntent(PlanDetailIntent.PlanApplyClick(false)) },
        )
    }

    if (uiState.isShowPlanEditDialog) {
        PlanDetailEditDialog(
            onIntent = onIntent,
        )
    }

    if (uiState.isShowPlanReportDialog) {
        PlanDetailReportDialog(
            onIntent = onIntent,
        )
    }

    if (uiState.isShowCommentEditDialog && uiState.selectedComment != null) {
        PlanDetailCommentEditDialog(
            comment = uiState.selectedComment,
            onIntent = onIntent,
        )
    }

    if (uiState.isShowCommentReportDialog && uiState.selectedComment != null) {
        PlanDetailCommentReportDialog(
            comment = uiState.selectedComment,
            onIntent = onIntent,
        )
    }

    LoadingDialog(isLoading)
}

@Composable
private fun PlanDetailSpacer() {
    Spacer(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(MoimTheme.colors.stroke),
    )
}
