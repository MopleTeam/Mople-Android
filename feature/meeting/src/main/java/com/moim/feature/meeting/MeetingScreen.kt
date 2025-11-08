package com.moim.feature.meeting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.common.model.Meeting
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.PagingErrorScreen
import com.moim.core.designsystem.common.PagingLoadingScreen
import com.moim.core.designsystem.component.MoimFloatingActionButton
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.view.ObserveAsEvents
import com.moim.core.ui.view.PAGING_ERROR
import com.moim.core.ui.view.PAGING_LOADING
import com.moim.core.ui.view.isAppendError
import com.moim.core.ui.view.isAppendLoading
import com.moim.core.ui.view.isError
import com.moim.core.ui.view.isLoading
import com.moim.core.ui.view.isSuccess
import com.moim.feature.meeting.ui.MeetingCard

@Composable
fun MeetingRoute(
    viewModel: MeetingViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToMeetingWrite: () -> Unit,
    navigateToMeetingDetail: (String) -> Unit
) {
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.white)
    val meetings = viewModel.meetings.collectAsLazyPagingItems(LocalLifecycleOwner.current.lifecycleScope.coroutineContext)

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is MeetingUiEvent.NavigateToMeetingWrite -> navigateToMeetingWrite()
            is MeetingUiEvent.NavigateToMeetingDetail -> navigateToMeetingDetail(event.meetingId)
            is MeetingUiEvent.RefreshPagingData -> meetings.refresh()
        }
    }

    MeetingScreen(
        modifier = modifier,
        meetings = meetings,
        onUiAction = viewModel::onUiAction
    )
}

@Composable
fun MeetingScreen(
    modifier: Modifier = Modifier,
    meetings: LazyPagingItems<Meeting>,
    onUiAction: (MeetingUiAction) -> Unit = {}
) {
    TrackScreenViewEvent(screenName = "meet_list")
    MoimScaffold(
        modifier = modifier,
        backgroundColor = MoimTheme.colors.bg.primary,
        topBar = {
            MoimTopAppbar(
                title = stringResource(R.string.meeting_title),
                isNavigationIconVisible = false
            )
        },
        content = { padding ->
            val contentModifier = Modifier
                .fillMaxSize()
                .padding(padding)

            Box(
                modifier = contentModifier,
            ) {
                AnimatedVisibility(
                    modifier = Modifier.fillMaxSize(),
                    enter = fadeIn(),
                    exit = fadeOut(),
                    visible = meetings.loadState.isSuccess() && meetings.itemCount > 0
                ) {
                    MeetingContent(
                        meetings = meetings,
                        onUiAction = onUiAction
                    )
                }

                AnimatedVisibility(
                    enter = fadeIn(),
                    exit = fadeOut(),
                    visible = meetings.loadState.isLoading(),
                ) {
                    PagingLoadingScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                    )
                }

                AnimatedVisibility(
                    modifier = Modifier.fillMaxSize(),
                    enter = fadeIn(),
                    exit = fadeOut(),
                    visible = meetings.loadState.isError()
                ) {
                    ErrorScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MoimTheme.colors.bg.primary),
                        onClickRefresh = { onUiAction(MeetingUiAction.OnClickRefresh) },
                    )
                }

                AnimatedVisibility(
                    modifier = Modifier.fillMaxSize(),
                    enter = fadeIn(),
                    exit = fadeOut(),
                    visible = meetings.loadState.isSuccess() && meetings.itemCount == 0
                ) {
                    MeetingEmptyScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MoimTheme.colors.bg.primary)
                    )
                }
            }
        },
        floatingActionButton = {
            MoimFloatingActionButton(
                minWidth = 54.dp,
                minHeight = 54.dp,
                onClick = { onUiAction(MeetingUiAction.OnClickMeetingWrite) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = ""
                )
            }
        }
    )
}

@Composable
fun MeetingContent(
    modifier: Modifier = Modifier,
    meetings: LazyPagingItems<Meeting>,
    onUiAction: (MeetingUiAction) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 28.dp, horizontal = 20.dp)
    ) {
        items(
            count = meetings.itemCount,
            key = meetings.itemKey(),
            contentType = meetings.itemContentType()
        ) { index ->
            val meeting = meetings[index] ?: return@items
            MeetingCard(
                modifier = Modifier.animateItem(),
                meeting = meeting,
                onUiAction = onUiAction
            )
        }

        if (meetings.loadState.isAppendLoading()) {
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

        if (meetings.loadState.isAppendError()) {
            item(key = PAGING_ERROR) {
                PagingErrorScreen(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(MoimTheme.colors.white)
                            .animateItem(),
                    onClickRetry = meetings::retry,
                )
            }
        }
    }
}

@Composable
fun MeetingEmptyScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(80.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_meeting),
            contentDescription = "",
            tint = MoimTheme.colors.icon
        )

        MoimText(
            text = stringResource(R.string.meeting_new_meeting),
            singleLine = false,
            style = MoimTheme.typography.body01.medium,
            color = MoimTheme.colors.gray.gray06
        )
    }
}