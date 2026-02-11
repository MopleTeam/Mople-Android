package com.moim.feature.alarm

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.common.model.ViewIdType
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.common.PagingErrorScreen
import com.moim.core.designsystem.common.PagingLoadingScreen
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.util.decimalFormatString
import com.moim.core.ui.view.FadeAnimatedVisibility
import com.moim.core.ui.view.ObserveAsEvents
import com.moim.core.ui.view.PaginationEffect
import com.moim.feature.alarm.ui.AlarmEmptyScreen
import com.moim.feature.alarm.ui.AlarmListItem

@Composable
fun AlarmRoute(
    padding: PaddingValues,
    viewModel: AlarmViewModel = hiltViewModel(),
    navigateToMeetingDetail: (String) -> Unit,
    navigateToPlanDetail: (ViewIdType) -> Unit,
    navigateToBack: () -> Unit,
) {
    val modifier =
        Modifier.containerScreen(
            backgroundColor = MoimTheme.colors.bg.primary,
            padding = padding,
        )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is AlarmUiEvent.NavigateToBack -> navigateToBack()
            is AlarmUiEvent.NavigateToMeetingDetail -> navigateToMeetingDetail(event.meetingId)
            is AlarmUiEvent.NavigateToPlanDetail -> navigateToPlanDetail(event.viewIdType)
        }
    }

    (uiState as? AlarmUiState)?.let {
        LaunchedEffect(Unit) {
            if (!it.pagingInfo.isLoading && !it.pagingInfo.isError) {
                viewModel.onUiAction(AlarmUiAction.OnUpdateNotificationCount)
            }
        }

        AlarmScreen(
            modifier = modifier,
            uiState = it,
            onUiAction = viewModel::onUiAction,
        )
    }
}

@Composable
private fun AlarmScreen(
    modifier: Modifier = Modifier,
    uiState: AlarmUiState,
    onUiAction: (AlarmUiAction) -> Unit,
) {
    val listState = rememberLazyListState()
    val paging = uiState.pagingInfo

    TrackScreenViewEvent(screenName = "notification")
    Column(
        modifier = modifier,
    ) {
        MoimTopAppbar(
            modifier = Modifier,
            title = stringResource(R.string.alarm_title),
            onClickNavigate = { onUiAction(AlarmUiAction.OnClickBack) },
        )

        AlarmCount(alarmCount = paging.totalCount)

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            FadeAnimatedVisibility(paging.isLoading) {
                LoadingScreen()
            }

            FadeAnimatedVisibility(paging.isError) {
                ErrorScreen {
                    onUiAction(AlarmUiAction.OnClickRefresh)
                }
            }

            FadeAnimatedVisibility(paging.isSuccess && uiState.alarms.isNotEmpty()) {
                PaginationEffect(
                    listState = listState,
                    threshold = 3,
                    enabled = !paging.isLast && !paging.isErrorFooter,
                    onNext = { onUiAction(AlarmUiAction.OnLoadNextPage) },
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 64.dp),
                    state = listState,
                ) {
                    items(
                        items = uiState.alarms,
                        key = { it.notificationId },
                    ) { uiModel ->
                        AlarmListItem(
                            modifier = Modifier.animateItem(),
                            alarm = uiModel,
                            onUiAction = onUiAction,
                        )
                    }

                    item {
                        Text(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                            text = stringResource(R.string.alarm_deadline_description),
                            style = MoimTheme.typography.body01.regular,
                            color = MoimTheme.colors.text.text03,
                            textAlign = TextAlign.Center,
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
                                onUiAction(AlarmUiAction.OnClickRefresh)
                            }
                        }
                    }
                }
            }

            FadeAnimatedVisibility(paging.isSuccess && uiState.alarms.isEmpty()) {
                AlarmEmptyScreen()
            }
        }
    }
}

@Composable
private fun AlarmCount(
    modifier: Modifier = Modifier,
    alarmCount: Int,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 28.dp, bottom = 16.dp),
    ) {
        MoimText(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.alarm_new),
            style = MoimTheme.typography.body01.medium,
            color = MoimTheme.colors.text.text01,
        )

        MoimText(
            text = stringResource(R.string.unit_count, alarmCount.decimalFormatString()),
            style = MoimTheme.typography.body01.medium,
            color = MoimTheme.colors.text.text01,
        )
    }
}
