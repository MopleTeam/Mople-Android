package com.moim.feature.meetingnoticedetail

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.common.model.NoticeType
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.common.NotFoundErrorScreen
import com.moim.core.designsystem.common.PagingErrorScreen
import com.moim.core.designsystem.common.PagingLoadingScreen
import com.moim.core.designsystem.component.MoimIconButton
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.util.toValidUrl
import com.moim.core.ui.view.FadeAnimatedVisibility
import com.moim.core.ui.view.ObserveAsEvents
import com.moim.core.ui.view.PaginationEffect
import com.moim.core.ui.view.showToast
import com.moim.feature.meetingnoticedetail.ui.MeetingNoticeDetailBottomBar
import com.moim.feature.meetingnoticedetail.ui.MeetingNoticeDetailCommentEditDialog
import com.moim.feature.meetingnoticedetail.ui.MeetingNoticeDetailCommentHeader
import com.moim.feature.meetingnoticedetail.ui.MeetingNoticeDetailCommentItem
import com.moim.feature.meetingnoticedetail.ui.MeetingNoticeDetailCommentReportDialog
import com.moim.feature.meetingnoticedetail.ui.MeetingNoticeDetailContent
import com.moim.feature.meetingnoticedetail.ui.MeetingNoticeDetailEditDialog

internal typealias OnMeetingNoticeDetailUiAction = (MeetingNoticeDetailUiAction) -> Unit

@Composable
fun MeetingNoticeDetailRoute(
    viewModel: MeetingNoticeDetailViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    navigateToMeetingNoticeWrite: (String, String) -> Unit,
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.bg.primary)
    val openBrowserErrorMessage = stringResource(R.string.common_error_open_browser)

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is MeetingNoticeDetailUiEvent.NavigateToBack -> {
                navigateToBack()
            }

            is MeetingNoticeDetailUiEvent.NavigateToMeetingNoticeWrite -> {
                navigateToMeetingNoticeWrite(event.meetId, event.noticeId)
            }

            is MeetingNoticeDetailUiEvent.NavigateToWebBrowser -> {
                runCatching {
                    context.startActivity(Intent(Intent.ACTION_VIEW, event.webLink.toValidUrl()))
                }.onFailure {
                    showToast(context, openBrowserErrorMessage)
                }
            }

            is MeetingNoticeDetailUiEvent.ShowToastMessage -> {
                showToast(context, event.message)
            }
        }
    }

    when (val state = uiState) {
        is MeetingNoticeDetailUiState.Loading -> {
            LoadingScreen(modifier)
        }

        is MeetingNoticeDetailUiState.Success -> {
            MeetingNoticeDetailScreen(
                modifier = modifier,
                uiState = state,
                isLoading = isLoading,
                onUiAction = viewModel::onUiAction,
            )
        }

        is MeetingNoticeDetailUiState.NotFoundError -> {
            NotFoundErrorScreen(
                modifier = modifier,
                onClickBack = { viewModel.onUiAction(MeetingNoticeDetailUiAction.OnClickBack) },
            )
        }

        is MeetingNoticeDetailUiState.CommonError -> {
            ErrorScreen(
                modifier = modifier,
                onClickRefresh = { viewModel.onUiAction(MeetingNoticeDetailUiAction.OnClickRefresh) },
            )
        }
    }
}

@Composable
private fun MeetingNoticeDetailScreen(
    modifier: Modifier = Modifier,
    uiState: MeetingNoticeDetailUiState.Success,
    isLoading: Boolean,
    onUiAction: OnMeetingNoticeDetailUiAction,
) {
    val comments = uiState.comments
    val pagingInfo = uiState.commentsPagingInfo
    val listState = rememberLazyListState()

    PaginationEffect(
        listState = listState,
        threshold = 3,
        enabled = !pagingInfo.isLast && !pagingInfo.isErrorFooter,
        onNext = { onUiAction(MeetingNoticeDetailUiAction.OnLoadNextCommentsPage) },
    )

    MoimScaffold(
        modifier =
            modifier
                .fillMaxSize()
                .imePadding(),
        topBar = {
            MoimTopAppbar(
                title = stringResource(R.string.meeting_notice_detail_title),
                onClickNavigate = { onUiAction(MeetingNoticeDetailUiAction.OnClickBack) },
                actions = {
                    if (uiState.isHostUser && uiState.notice.type == NoticeType.CUSTOM) {
                        MoimIconButton(
                            iconRes = R.drawable.ic_more_bold,
                            onClick = { onUiAction(MeetingNoticeDetailUiAction.OnShowNoticeEditDialog(true)) },
                        )
                    }
                },
            )
        },
        content = { paddingValues ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                ) {
                    item {
                        MeetingNoticeDetailContent(notice = uiState.notice)
                    }

                    item {
                        MeetingNoticeDetailCommentHeader(
                            commentCount = pagingInfo.totalCount,
                        )
                    }

                    items(
                        items = comments,
                        key = { it.comment.commentId },
                    ) { commentUiModel ->
                        MeetingNoticeDetailCommentItem(
                            modifier = Modifier.animateItem(),
                            userId = uiState.user.userId,
                            comment = commentUiModel,
                            onUiAction = onUiAction,
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
                                onClickRetry = { onUiAction(MeetingNoticeDetailUiAction.OnLoadNextCommentsPage) },
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            MeetingNoticeDetailBottomBar(
                commentState = uiState.commentState,
                onUiAction = onUiAction,
            )
        },
    )

    if (uiState.isShowNoticeEditDialog) {
        MeetingNoticeDetailEditDialog(
            onUiAction = onUiAction,
        )
    }

    if (uiState.isShowCommentEditDialog && uiState.selectedComment != null) {
        MeetingNoticeDetailCommentEditDialog(
            comment = uiState.selectedComment,
            onUiAction = onUiAction,
        )
    }

    if (uiState.isShowCommentReportDialog && uiState.selectedComment != null) {
        MeetingNoticeDetailCommentReportDialog(
            comment = uiState.selectedComment,
            onUiAction = onUiAction,
        )
    }

    LoadingDialog(isLoading)
}
