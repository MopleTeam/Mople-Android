package com.moim.feature.meetingnotice

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moim.core.common.model.NoticeType
import com.moim.core.common.model.User
import com.moim.core.designsystem.R
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.component.MoimIconButton
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.view.FadeAnimatedVisibility
import com.moim.core.ui.view.PaginationEffect
import com.moim.core.ui.view.PagingUiState
import com.moim.core.ui.view.showToast
import com.moim.feature.meetingnotice.model.MeetingNoticeIntent
import com.moim.feature.meetingnotice.model.MeetingNoticeSideEffect
import com.moim.feature.meetingnotice.model.MeetingNoticeState
import com.moim.feature.meetingnotice.model.NoticeTabState
import com.moim.feature.meetingnotice.model.NoticeUiModel
import com.moim.feature.meetingnotice.ui.MeetingNoticeItem
import com.moim.feature.meetingnotice.ui.MeetingNoticeTabPager
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.time.ZonedDateTime

@Composable
fun MeetingNoticeRoute(
    viewModel: MeetingNoticeViewModel,
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    navigateToMeetingNoticeWrite: (String) -> Unit,
    navigateToMeetingNoticeDetail: (String, String) -> Unit,
) {
    val context = LocalContext.current
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.bg.primary)
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MeetingNoticeSideEffect.NavigateToBack -> navigateToBack()
            is MeetingNoticeSideEffect.NavigateToMeetingNoticeWrite -> navigateToMeetingNoticeWrite(sideEffect.meetId)
            is MeetingNoticeSideEffect.NavigateToMeetingNoticeDetail ->
                navigateToMeetingNoticeDetail(sideEffect.meetId, sideEffect.noticeId)

            is MeetingNoticeSideEffect.ShowToastMessage -> showToast(context, sideEffect.message)
        }
    }

    MeetingNoticeScreen(
        modifier = modifier,
        uiState = uiState,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun MeetingNoticeScreen(
    uiState: MeetingNoticeState,
    onIntent: (MeetingNoticeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val paging = uiState.currentTab.pagingInfo
    var openedNoticeId by remember { mutableStateOf<String?>(null) }
    val pagerState =
        rememberPagerState(
            initialPage = uiState.selectedTabIndex,
            pageCount = { MEETING_NOTICE_TAB_COUNT },
        )
    val listStates = List(MEETING_NOTICE_TAB_COUNT) { rememberLazyListState() }
    val currentListState = listStates[pagerState.currentPage]

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onIntent(MeetingNoticeIntent.TabSelect(page))
        }
    }

    MoimScaffold(
        modifier = modifier.fillMaxSize(),
        backgroundColor = MoimTheme.colors.bg.primary,
        topBar = {
            MoimTopAppbar(
                onClickNavigate = { onIntent(MeetingNoticeIntent.BackClick) },
                actions = {
                    if (uiState.isHostUser) {
                        MoimIconButton(
                            iconRes = R.drawable.ic_pen,
                            iconSize = 40.dp,
                            onClick = { onIntent(MeetingNoticeIntent.WriteClick) },
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
                    FadeAnimatedVisibility(uiState.isLoading) {
                        LoadingScreen()
                    }

                    FadeAnimatedVisibility(uiState.isError) {
                        ErrorScreen {
                            onIntent(MeetingNoticeIntent.RefreshClick)
                        }
                    }

                    FadeAnimatedVisibility(uiState.isSuccess) {
                        PaginationEffect(
                            listState = currentListState,
                            threshold = 3,
                            enabled = !paging.isLast && !paging.isErrorFooter,
                            onNext = { onIntent(MeetingNoticeIntent.NextPageLoad) },
                        )

                        HorizontalPager(
                            modifier = Modifier.fillMaxSize(),
                            state = pagerState,
                        ) { page ->
                            val pageNotices = uiState.tabStates[page]?.notices ?: emptyList()
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                state = listStates[page],
                            ) {
                                items(
                                    items = pageNotices,
                                    key = { it.noticeId },
                                ) { notice ->
                                    MeetingNoticeItem(
                                        notice = notice,
                                        isHostUser = uiState.isHostUser,
                                        openedNoticeId = openedNoticeId,
                                        onOpenedChange = { openedNoticeId = it },
                                        onIntent = onIntent,
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
            NoticeUiModel(
                noticeId = "",
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
                    pinned = true,
                    createdAt = ZonedDateTime.now().minusDays(1),
                ),
                notice.copy(
                    noticeId = "2",
                    type = NoticeType.SYSTEM,
                    content = "모임장이 카카오님에서 붕어빵님으로 변경되었습니다.",
                    createdAt = ZonedDateTime.now().minusDays(2),
                ),
                notice.copy(
                    noticeId = "3",
                    createdAt = ZonedDateTime.now().minusDays(3),
                ),
            )

        MeetingNoticeScreen(
            uiState =
                MeetingNoticeState(
                    user = User(userId = "", nickname = ""),
                    isHostUser = true,
                    tabStates =
                        mapOf(
                            0 to
                                NoticeTabState(
                                    notices = notices,
                                    pagingInfo = PagingUiState(isLoading = false),
                                    isLoaded = true,
                                ),
                        ),
                ),
            onIntent = {},
        )
    }
}
