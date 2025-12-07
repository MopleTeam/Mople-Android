package com.moim.feature.commentdetail

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.moim.core.ui.util.toValidUrl
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
import com.moim.core.ui.view.ObserveAsEvents
import com.moim.core.ui.view.PAGING_ERROR
import com.moim.core.ui.view.PAGING_LOADING
import com.moim.core.ui.view.isAppendError
import com.moim.core.ui.view.isAppendLoading
import com.moim.core.ui.view.isError
import com.moim.core.ui.view.isLoading
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
        defaultImage: Int
    ) -> Unit,
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val commentDetailUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.white)

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is CommentDetailUiEvent.NavigateToBack -> navigateToBack()

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

            is CommentDetailUiEvent.ShowToastMessage -> showToast(context, event.message)
        }
    }

    when (val uiState = commentDetailUiState) {
        is CommentDetailUiState.Loading -> {
            LoadingScreen(modifier)
        }

        is CommentDetailUiState.Success -> {
            CommentDetailScreen(
                modifier = modifier,
                uiState = uiState,
                isLoading = isLoading,
                onUiAction = viewModel::onUiAction
            )
        }

        is CommentDetailUiState.NotFoundError -> {
            NotFoundErrorScreen(
                modifier = modifier,
                description = stringResource(R.string.comment_detail_not_found_error),
                onClickBack = { viewModel.onUiAction(CommentDetailAction.OnClickBack) }
            )
        }

        is CommentDetailUiState.CommonError -> {
            ErrorScreen(
                modifier = modifier,
                onClickRefresh = { viewModel.onUiAction(CommentDetailAction.OnClickRefresh) }
            )
        }
    }
}

@Composable
fun CommentDetailScreen(
    modifier: Modifier = Modifier,
    uiState: CommentDetailUiState.Success,
    isLoading: Boolean,
    onUiAction: (CommentDetailAction) -> Unit
) {
    val comments = uiState.replyComments?.collectAsLazyPagingItems(LocalLifecycleOwner.current.lifecycleScope.coroutineContext)

    MoimScaffold(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            CommentDetailTopAppbar(
                modifier = Modifier.fillMaxWidth(),
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
                    if (comments != null) {
                        items(
                            count = comments.itemCount,
                            key = comments.itemKey(),
                            contentType = comments.itemContentType(),
                        ) { index ->
                            val comment = comments[index] ?: return@items

                            CommentDetailItem(
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
                    CommentDetailMentionDialog(
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
            CommentDetailBottomBar(
                updateComment = uiState.selectedUpdateComment,
                commentState = uiState.commentState,
                selectedMentions = uiState.selectedMentions,
                onUiAction = onUiAction
            )
        }
    )

    if (uiState.isShowCommentEditDialog && uiState.selectedComment != null) {
        CommentDetailEditDialog(
            comment = uiState.selectedComment,
            onUiAction = onUiAction
        )
    }

    if (uiState.isShowCommentReportDialog && uiState.selectedComment != null) {
        CommentDetailReportDialog(
            comment = uiState.selectedComment,
            onUiAction = onUiAction
        )
    }

    LoadingDialog(isLoading)
}