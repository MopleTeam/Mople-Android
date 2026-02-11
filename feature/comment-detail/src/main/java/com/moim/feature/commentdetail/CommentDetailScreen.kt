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
import com.moim.core.ui.view.ObserveAsEvents
import com.moim.core.ui.view.PaginationEffect
import com.moim.core.ui.view.showToast
import com.moim.feature.commentdetail.ui.CommentDetailBottomBar
import com.moim.feature.commentdetail.ui.CommentDetailEditDialog
import com.moim.feature.commentdetail.ui.CommentDetailItem
import com.moim.feature.commentdetail.ui.CommentDetailMentionDialog
import com.moim.feature.commentdetail.ui.CommentDetailReportDialog
import com.moim.feature.commentdetail.ui.CommentDetailTopAppbar

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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.bg.primary)

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is CommentDetailUiEvent.NavigateToBack -> {
                navigateToBack()
            }

            is CommentDetailUiEvent.NavigateToImageViewerForUser -> {
                navigateToImageViewer(event.userName, listOf(event.image), 0, R.drawable.ic_empty_user_logo)
            }

            is CommentDetailUiEvent.NavigateToWebBrowser -> {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, event.webLink.toValidUrl()))
                } catch (e: Exception) {
                    showToast(context, context.getString(R.string.common_error_open_browser))
                }
            }

            is CommentDetailUiEvent.ShowToastMessage -> {
                showToast(context, event.message)
            }
        }
    }

    (uiState as? CommentDetailUiState)?.let { uiState ->
        CommentDetailScreen(
            modifier = modifier,
            uiState = uiState,
            isLoading = isLoading,
            onUiAction = viewModel::onUiAction,
        )
    }
}

@Composable
fun CommentDetailScreen(
    modifier: Modifier = Modifier,
    uiState: CommentDetailUiState,
    isLoading: Boolean,
    onUiAction: (CommentDetailUiAction) -> Unit,
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
                onUiAction = onUiAction,
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
                FadeAnimatedVisibility(paging.isLoading) {
                    LoadingScreen()
                }

                FadeAnimatedVisibility(paging.isError) {
                    if (uiState.isNotFoundError) {
                        NotFoundErrorScreen(
                            modifier = modifier,
                            description = stringResource(R.string.comment_detail_not_found_error),
                            onClickBack = { onUiAction(CommentDetailUiAction.OnClickBack) },
                        )
                    } else {
                        ErrorScreen {
                            onUiAction(CommentDetailUiAction.OnClickRefresh)
                        }
                    }
                }

                FadeAnimatedVisibility(uiState.pagingInfo.isSuccess && uiState.replyComments.isNotEmpty()) {
                    PaginationEffect(
                        listState = listState,
                        threshold = 3,
                        enabled = !paging.isLast && !paging.isErrorFooter,
                        onNext = { onUiAction(CommentDetailUiAction.OnLoadNextPage) },
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
                                onUiAction = onUiAction,
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
                                onUiAction = onUiAction,
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
                                    onUiAction(CommentDetailUiAction.OnClickRefresh)
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
                        onUiAction = onUiAction,
                    )
                }
            }
        },
        bottomBar = {
            CommentDetailBottomBar(
                updateComment = uiState.selectedUpdateComment,
                commentState = uiState.commentState,
                selectedMentions = uiState.selectedMentions,
                onUiAction = onUiAction,
            )
        },
    )

    if (uiState.isShowCommentEditDialog && uiState.selectedComment != null) {
        CommentDetailEditDialog(
            comment = uiState.selectedComment,
            onUiAction = onUiAction,
        )
    }

    if (uiState.isShowCommentReportDialog && uiState.selectedComment != null) {
        CommentDetailReportDialog(
            comment = uiState.selectedComment,
            onUiAction = onUiAction,
        )
    }

    LoadingDialog(isLoading)
}
