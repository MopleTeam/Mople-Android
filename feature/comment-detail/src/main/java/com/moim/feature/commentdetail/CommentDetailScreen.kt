package com.moim.feature.commentdetail

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.common.NotFoundErrorScreen
import com.moim.core.designsystem.common.PagingErrorScreen
import com.moim.core.designsystem.common.PagingLoadingScreen
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.util.toValidUrl
import com.moim.core.ui.view.FadeAnimatedVisibility
import com.moim.core.ui.view.PaginationEffect
import com.moim.core.ui.view.showToast
import com.moim.feature.commentdetail.model.CommentDetailIntent
import com.moim.feature.commentdetail.model.CommentDetailSideEffect
import com.moim.feature.commentdetail.model.CommentDetailState
import com.moim.feature.commentdetail.ui.CommentDetailBottomBar
import com.moim.feature.commentdetail.ui.CommentDetailEditDialog
import com.moim.feature.commentdetail.ui.CommentDetailItem
import com.moim.feature.commentdetail.ui.CommentDetailMentionDialog
import com.moim.feature.commentdetail.ui.CommentDetailReportDialog
import com.moim.feature.commentdetail.ui.CommentDetailTopAppbar
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun CommentDetailRoute(
    viewModel: CommentDetailViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToBack: () -> Unit,
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
            is CommentDetailSideEffect.NavigateToBack -> {
                navigateToBack()
            }

            is CommentDetailSideEffect.NavigateToImageViewerForUser -> {
                navigateToImageViewer(sideEffect.userName, listOf(sideEffect.image), 0, R.drawable.ic_empty_user_logo)
            }

            is CommentDetailSideEffect.NavigateToWebBrowser -> {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, sideEffect.webLink.toValidUrl()))
                } catch (e: Exception) {
                    showToast(context, context.getString(R.string.common_error_open_browser))
                }
            }

            is CommentDetailSideEffect.ShowToastMessage -> {
                showToast(context, sideEffect.message)
            }
        }
    }

    CommentDetailScreen(
        modifier = modifier,
        uiState = uiState,
        isLoading = isLoading,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun CommentDetailScreen(
    modifier: Modifier = Modifier,
    uiState: CommentDetailState,
    isLoading: Boolean,
    onIntent: (CommentDetailIntent) -> Unit,
) {
    val listState = rememberLazyListState()
    val paging = uiState.pagingInfo

    MoimScaffold(
        modifier =
            modifier
                .fillMaxSize()
                .imePadding(),
        topBar = {
            CommentDetailTopAppbar(
                modifier = Modifier.fillMaxWidth(),
                onIntent = onIntent,
            )
        },
        content = {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(it),
                contentAlignment = Alignment.Center,
            ) {
                FadeAnimatedVisibility(uiState.isLoading) {
                    LoadingScreen()
                }

                FadeAnimatedVisibility(uiState.isError) {
                    if (uiState.isNotFoundError) {
                        NotFoundErrorScreen(
                            modifier = modifier,
                            description = stringResource(R.string.comment_detail_not_found_error),
                            onClickBack = { onIntent(CommentDetailIntent.BackClick) },
                        )
                    } else {
                        ErrorScreen {
                            onIntent(CommentDetailIntent.RefreshClick)
                        }
                    }
                }

                FadeAnimatedVisibility(uiState.isSuccess) {
                    PaginationEffect(
                        listState = listState,
                        threshold = 3,
                        enabled = !paging.isLast && !paging.isErrorFooter,
                        onNext = { onIntent(CommentDetailIntent.NextPageLoad) },
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                    ) {
                        item {
                            CommentDetailItem(
                                modifier = Modifier.animateItem(),
                                userId = uiState.user.userId,
                                comment = uiState.parentComment,
                                onIntent = onIntent,
                            )
                        }

                        items(
                            items = uiState.replyComments,
                            key = { uiModel -> uiModel.commentId },
                        ) { comment ->
                            CommentDetailItem(
                                modifier = Modifier.animateItem(),
                                userId = uiState.user.userId,
                                comment = comment,
                                onIntent = onIntent,
                            )
                        }

                        item {
                            if (paging.isLoadingFooter) {
                                PagingLoadingScreen(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .animateItem(),
                                )
                            }
                        }

                        item {
                            if (paging.isErrorFooter) {
                                PagingErrorScreen(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .animateItem(),
                                ) {
                                    onIntent(CommentDetailIntent.RefreshClick)
                                }
                            }
                        }
                    }
                }

                if (uiState.isShowMentionDialog) {
                    CommentDetailMentionDialog(
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
            CommentDetailBottomBar(
                updateComment = uiState.selectedUpdateComment,
                commentState = uiState.commentState,
                selectedMentions = uiState.selectedMentions,
                onIntent = onIntent,
            )
        },
    )

    if (uiState.isShowCommentEditDialog && uiState.selectedComment != null) {
        CommentDetailEditDialog(
            comment = uiState.selectedComment,
            onIntent = onIntent,
        )
    }

    if (uiState.isShowCommentReportDialog && uiState.selectedComment != null) {
        CommentDetailReportDialog(
            comment = uiState.selectedComment,
            onIntent = onIntent,
        )
    }

    LoadingDialog(isLoading)
}
