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
import com.moim.core.common.result.data
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
import com.moim.core.ui.view.PaginationEffect
import com.moim.core.ui.view.showToast
import com.moim.feature.meetingnoticedetail.model.MeetingNoticeDetailIntent
import com.moim.feature.meetingnoticedetail.model.MeetingNoticeDetailSideEffect
import com.moim.feature.meetingnoticedetail.model.MeetingNoticeDetailState
import com.moim.feature.meetingnoticedetail.ui.MeetingNoticeDetailBottomBar
import com.moim.feature.meetingnoticedetail.ui.MeetingNoticeDetailCommentEditDialog
import com.moim.feature.meetingnoticedetail.ui.MeetingNoticeDetailCommentHeader
import com.moim.feature.meetingnoticedetail.ui.MeetingNoticeDetailCommentItem
import com.moim.feature.meetingnoticedetail.ui.MeetingNoticeDetailCommentReportDialog
import com.moim.feature.meetingnoticedetail.ui.MeetingNoticeDetailContent
import com.moim.feature.meetingnoticedetail.ui.MeetingNoticeDetailEditDialog
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

internal typealias OnMeetingNoticeDetailIntent = (MeetingNoticeDetailIntent) -> Unit

@Composable
fun MeetingNoticeDetailRoute(
    viewModel: MeetingNoticeDetailViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    navigateToMeetingNoticeWrite: (String, String) -> Unit,
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val uiState by viewModel.collectAsState()
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.bg.primary)
    val openBrowserErrorMessage = stringResource(R.string.common_error_open_browser)

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MeetingNoticeDetailSideEffect.NavigateToBack -> {
                navigateToBack()
            }

            is MeetingNoticeDetailSideEffect.NavigateToMeetingNoticeWrite -> {
                navigateToMeetingNoticeWrite(sideEffect.meetId, sideEffect.noticeId)
            }

            is MeetingNoticeDetailSideEffect.NavigateToWebBrowser -> {
                runCatching {
                    context.startActivity(Intent(Intent.ACTION_VIEW, sideEffect.webLink.toValidUrl()))
                }.onFailure {
                    showToast(context, openBrowserErrorMessage)
                }
            }

            is MeetingNoticeDetailSideEffect.ShowToastMessage -> {
                showToast(context, sideEffect.message)
            }
        }
    }

    when {
        uiState.isLoading -> {
            LoadingScreen(modifier)
        }

        uiState.isSuccess -> {
            MeetingNoticeDetailScreen(
                modifier = modifier,
                uiState = uiState,
                isLoading = isLoading,
                onIntent = viewModel::onIntent,
            )
        }

        uiState.isNotFoundError -> {
            NotFoundErrorScreen(
                modifier = modifier,
                onClickBack = { viewModel.onIntent(MeetingNoticeDetailIntent.BackClick) },
            )
        }

        uiState.isError -> {
            ErrorScreen(
                modifier = modifier,
                onClickRefresh = { viewModel.onIntent(MeetingNoticeDetailIntent.RefreshClick) },
            )
        }
    }
}

@Composable
private fun MeetingNoticeDetailScreen(
    modifier: Modifier = Modifier,
    uiState: MeetingNoticeDetailState,
    isLoading: Boolean,
    onIntent: OnMeetingNoticeDetailIntent,
) {
    val notice = uiState.notice.data ?: return
    val comments = uiState.comments
    val pagingInfo = uiState.commentsPagingInfo
    val listState = rememberLazyListState()

    PaginationEffect(
        listState = listState,
        threshold = 3,
        enabled = !pagingInfo.isLast && !pagingInfo.isErrorFooter,
        onNext = { onIntent(MeetingNoticeDetailIntent.NextCommentsPageLoad) },
    )

    MoimScaffold(
        modifier =
            modifier
                .fillMaxSize()
                .imePadding(),
        topBar = {
            MoimTopAppbar(
                title = stringResource(R.string.meeting_notice_detail_title),
                onClickNavigate = { onIntent(MeetingNoticeDetailIntent.BackClick) },
                actions = {
                    if (uiState.isHostUser && notice.type == NoticeType.CUSTOM) {
                        MoimIconButton(
                            iconRes = R.drawable.ic_more_bold,
                            onClick = { onIntent(MeetingNoticeDetailIntent.NoticeEditDialogShow(true)) },
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
                        MeetingNoticeDetailContent(notice = notice)
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
                                onClickRetry = { onIntent(MeetingNoticeDetailIntent.NextCommentsPageLoad) },
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            MeetingNoticeDetailBottomBar(
                commentState = uiState.commentState,
                onIntent = onIntent,
            )
        },
    )

    if (uiState.isShowNoticeEditDialog) {
        MeetingNoticeDetailEditDialog(
            onIntent = onIntent,
        )
    }

    if (uiState.isShowCommentEditDialog && uiState.selectedComment != null) {
        MeetingNoticeDetailCommentEditDialog(
            comment = uiState.selectedComment,
            onIntent = onIntent,
        )
    }

    if (uiState.isShowCommentReportDialog && uiState.selectedComment != null) {
        MeetingNoticeDetailCommentReportDialog(
            comment = uiState.selectedComment,
            onIntent = onIntent,
        )
    }

    LoadingDialog(isLoading)
}
