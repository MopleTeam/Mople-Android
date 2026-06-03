package com.moim.feature.meetingnotice

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.common.model.Notice
import com.moim.core.common.model.NoticeType
import com.moim.core.common.model.User
import com.moim.core.designsystem.R
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.view.FadeAnimatedVisibility
import com.moim.core.ui.view.ObserveAsEvents
import com.moim.core.ui.view.PaginationEffect
import com.moim.core.ui.view.PagingUiState
import com.moim.feature.meetingnotice.ui.MeetingNoticeItem
import com.moim.feature.meetingnotice.ui.MeetingNoticeTabPager
import java.time.ZonedDateTime

@Composable
fun MeetingNoticeRoute(
    viewModel: MeetingNoticeViewModel,
    padding: PaddingValues,
    navigateToBack: () -> Unit,
) {
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.bg.primary)
    val noticeUiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is MeetingNoticeUiEvent.NavigateToBack -> navigateToBack()
        }
    }

    (noticeUiState as? MeetingNoticeUiState)?.let { uiState ->
        MeetingNoticeScreen(
            modifier = modifier,
            uiState = uiState,
            onUiAction = viewModel::onUiAction,
        )
    }
}

@Composable
private fun MeetingNoticeScreen(
    uiState: MeetingNoticeUiState,
    onUiAction: (MeetingNoticeUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val paging = uiState.pagingInfo
    val pagerState =
        rememberPagerState(
            initialPage = uiState.selectedTabIndex,
            pageCount = { MEETING_NOTICE_TAB_COUNT },
        )
    val listStates = List(MEETING_NOTICE_TAB_COUNT) { rememberLazyListState() }
    val currentListState = listStates[pagerState.currentPage]

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onUiAction(MeetingNoticeUiAction.OnTabSelected(page))
        }
    }

    MoimScaffold(
        modifier = modifier.fillMaxSize(),
        backgroundColor = MoimTheme.colors.bg.primary,
        topBar = {
            MoimTopAppbar(
                onClickNavigate = { onUiAction(MeetingNoticeUiAction.OnClickBack) },
                actions = {
                    if (uiState.isHostUser) {
                        Icon(
                            modifier = Modifier.size(40.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.ic_pen),
                            contentDescription = "",
                            tint = MoimTheme.colors.icon,
                        )
                    }
                },
                title = stringResource(R.string.meeting_notice_title),
            )
        },
        content = { padding ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
            ) {
                Spacer(Modifier.height(8.dp))
                MeetingNoticeTabPager(pagerState = pagerState)

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    FadeAnimatedVisibility(paging.isLoading) {
                        LoadingScreen()
                    }

                    FadeAnimatedVisibility(paging.isError) {
                        ErrorScreen {
                            onUiAction(MeetingNoticeUiAction.OnClickRefresh)
                        }
                    }

                    FadeAnimatedVisibility(paging.isSuccess) {
                        PaginationEffect(
                            listState = currentListState,
                            threshold = 3,
                            enabled = !paging.isLast && !paging.isErrorFooter,
                            onNext = { onUiAction(MeetingNoticeUiAction.OnLoadNextPage) },
                        )

                        HorizontalPager(
                            modifier = Modifier.fillMaxSize(),
                            state = pagerState,
                        ) { page ->
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                state = listStates[page],
                            ) {
                                items(
                                    items = uiState.notices,
                                    key = { it.noticeId },
                                ) { notice ->
                                    MeetingNoticeItem(
                                        notice = notice,
                                        onUiAction = onUiAction,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
    )
}

@ThemePreviews
@Composable
private fun MeetingNoticeScreenPreview() {
    MoimTheme {
        val notice =
            Notice(
                noticeId = "",
                version = 1,
                meetId = "",
                type = NoticeType.CUSTOM,
                content = "11/28일 모임 18:00 → 20:00 변경, 날씨이슈로 인해서 부득이하게 변경했습니다!",
                createdAt = ZonedDateTime.now(),
                pinned = false,
            )

        val notices =
            listOf(
                notice.copy(
                    noticeId = "1",
                    type = NoticeType.SYSTEM,
                    createdAt = ZonedDateTime.now().minusDays(1),
                ),
                notice.copy(
                    noticeId = "2",
                    createdAt = ZonedDateTime.now().minusDays(2),
                ),
                notice.copy(
                    noticeId = "3",
                    createdAt = ZonedDateTime.now().minusDays(3),
                ),
            )

        MeetingNoticeScreen(
            uiState =
                MeetingNoticeUiState(
                    user = User(userId = "", nickname = ""),
                    isHostUser = true,
                    notices = notices,
                    pagingInfo =
                        PagingUiState(
                            isLoading = false,
                        ),
                ),
            onUiAction = {},
        )
    }
}
