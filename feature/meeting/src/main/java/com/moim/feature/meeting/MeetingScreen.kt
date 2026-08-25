package com.moim.feature.meeting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.common.PagingErrorScreen
import com.moim.core.designsystem.common.PagingLoadingScreen
import com.moim.core.designsystem.component.MoimFloatingActionButton
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.view.FadeAnimatedVisibility
import com.moim.core.ui.view.PaginationEffect
import com.moim.feature.meeting.model.MeetingIntent
import com.moim.feature.meeting.model.MeetingSideEffect
import com.moim.feature.meeting.model.MeetingState
import com.moim.feature.meeting.ui.MeetingCard
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun MeetingRoute(
    viewModel: MeetingViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToMeetingWrite: () -> Unit,
    navigateToMeetingDetail: (String) -> Unit,
) {
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.bg.primary)
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MeetingSideEffect.NavigateToMeetingWrite -> navigateToMeetingWrite()
            is MeetingSideEffect.NavigateToMeetingDetail -> navigateToMeetingDetail(sideEffect.meetingId)
        }
    }

    MeetingScreen(
        modifier = modifier,
        uiState = uiState,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun MeetingScreen(
    modifier: Modifier = Modifier,
    uiState: MeetingState,
    onIntent: (MeetingIntent) -> Unit = {},
) {
    val listState = rememberLazyListState()
    val paging = uiState.pagingInfo

    TrackScreenViewEvent(screenName = "meet_list")
    MoimScaffold(
        modifier = modifier,
        backgroundColor = MoimTheme.colors.bg.secondary,
        topBar = {
            MoimTopAppbar(
                title = stringResource(R.string.meeting_title),
                isNavigationIconVisible = false,
                backgroundColor = MoimTheme.colors.bg.primary,
            )
        },
        content = { padding ->
            val contentModifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)

            Box(
                modifier = contentModifier,
                contentAlignment = Alignment.Center,
            ) {
                FadeAnimatedVisibility(uiState.isLoading) {
                    LoadingScreen()
                }

                FadeAnimatedVisibility(uiState.isError) {
                    ErrorScreen {
                        onIntent(MeetingIntent.RefreshClick)
                    }
                }

                FadeAnimatedVisibility(uiState.isSuccess && uiState.meetings.isNotEmpty()) {
                    PaginationEffect(
                        listState = listState,
                        threshold = 3,
                        enabled = !paging.isLast && !paging.isErrorFooter,
                        onNext = { onIntent(MeetingIntent.NextPageLoad) },
                    )

                    LazyColumn(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(top = 28.dp, bottom = 90.dp),
                    ) {
                        items(
                            items = uiState.meetings,
                            key = { it.meeting.id },
                        ) { uiModel ->
                            MeetingCard(
                                modifier = Modifier.animateItem(),
                                uiModel = uiModel,
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
                                    backgroundColor = MoimTheme.colors.bg.secondary,
                                ) {
                                    onIntent(MeetingIntent.RefreshClick)
                                }
                            }
                        }
                    }
                }

                FadeAnimatedVisibility(uiState.isSuccess && uiState.meetings.isEmpty()) {
                    MeetingEmptyScreen(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(MoimTheme.colors.bg.primary),
                    )
                }
            }
        },
        floatingActionButton = {
            MoimFloatingActionButton(
                minWidth = 54.dp,
                minHeight = 54.dp,
                onClick = { onIntent(MeetingIntent.MeetingWriteClick) },
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_add),
                    contentDescription = "",
                )
            }
        },
    )
}

@Composable
private fun MeetingEmptyScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier.size(80.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_meeting),
            contentDescription = "",
            tint = MoimTheme.colors.icon,
        )

        MoimText(
            text = stringResource(R.string.meeting_new_meeting),
            singleLine = false,
            style = MoimTheme.typography.body01.medium,
            color = MoimTheme.colors.text.text04,
        )
    }
}
