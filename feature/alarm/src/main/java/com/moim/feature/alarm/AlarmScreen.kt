package com.moim.feature.alarm

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.common.view.ObserveAsEvents
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.alarm.ui.AlarmEmptyScreen
import com.moim.feature.alarm.ui.AlarmListItem

@Composable
fun AlarmRoute(
    padding: PaddingValues,
    viewModel: AlarmViewModel = hiltViewModel(),
    navigateToMeetingDetail: (String) -> Unit,
    navigateToPlanDetail: (String, Boolean) -> Unit,
    navigateToBack: () -> Unit,
) {
    val alarmUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.white, padding = padding)

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is AlarmUiEvent.NavigateToBack -> navigateToBack()
            is AlarmUiEvent.NavigateToMeetingDetail -> navigateToMeetingDetail(event.meetingId)
            is AlarmUiEvent.NavigateToPlanDetail -> navigateToPlanDetail(event.postId, event.isPlan)
        }
    }

    LaunchedEffect(alarmUiState) {
        if (alarmUiState !is AlarmUiState.Success) return@LaunchedEffect
        viewModel.onUiAction(AlarmUiAction.OnUpdateNotificationCount)
    }

    when (val uiState = alarmUiState) {
        is AlarmUiState.Loading -> LoadingScreen(modifier)

        is AlarmUiState.Success -> AlarmScreen(
            modifier = modifier,
            uiState = uiState,
            onUiAction = viewModel::onUiAction
        )

        is AlarmUiState.Error -> ErrorScreen(
            modifier = modifier,
            onClickRefresh = { viewModel.onUiAction(AlarmUiAction.OnClickRefresh) }
        )
    }
}

@Composable
fun AlarmScreen(
    modifier: Modifier = Modifier,
    uiState: AlarmUiState.Success,
    onUiAction: (AlarmUiAction) -> Unit
) {
    TrackScreenViewEvent(screenName = "notification")
    Column(
        modifier = modifier
    ) {
        MoimTopAppbar(
            modifier = Modifier,
            title = stringResource(R.string.alarm_title),
            onClickNavigate = { onUiAction(AlarmUiAction.OnClickBack) }
        )

        AlarmCount(alarmCount = uiState.notifications.size)

        if (uiState.notifications.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 64.dp)
            ) {
                items(
                    items = uiState.notifications,
                    key = { it.notificationId }
                ) { notification ->
                    AlarmListItem(
                        notification = notification,
                        onUiAction = onUiAction
                    )
                }
            }
        } else {
            AlarmEmptyScreen()
        }
    }
}

@Composable
fun AlarmCount(
    modifier: Modifier = Modifier,
    alarmCount: Int,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 28.dp, bottom = 16.dp)
    ) {
        MoimText(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.alarm_new),
            style = MoimTheme.typography.body01.medium,
            color = MoimTheme.colors.gray.gray01
        )

        MoimText(
            text = stringResource(R.string.unit_count, alarmCount),
            style = MoimTheme.typography.body01.medium,
            color = MoimTheme.colors.gray.gray01
        )
    }
}

@Preview
@Composable
private fun AlarmScreenPreview() {
    MoimTheme {
        AlarmScreen(
            modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.white),
            uiState = AlarmUiState.Success(),
            onUiAction = {}
        )
    }
}