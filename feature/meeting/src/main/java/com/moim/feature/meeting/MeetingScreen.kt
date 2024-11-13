package com.moim.feature.meeting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.common.view.ObserveAsEvents
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.component.MoimFloatingActionButton
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.model.Meeting
import com.moim.feature.meeting.ui.MeetingCard

internal typealias OnMeetingUiAction = (MeetingUiAction) -> Unit

@Composable
fun MeetingRoute(
    viewModel: MeetingViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToMeetingWrite: () -> Unit,
    navigateToMeetingDetail: (String) -> Unit
) {
    val meetingUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modifier = Modifier.containerScreen(padding)

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is MeetingUiEvent.NavigateToMeetingWrite -> navigateToMeetingWrite()
            is MeetingUiEvent.NavigateToMeetingDetail -> navigateToMeetingDetail(event.meetingId)
        }
    }

    when (val uiState = meetingUiState) {
        is MeetingUiState.Loading -> LoadingScreen(modifier = modifier)

        is MeetingUiState.Success -> MeetingScreen(
            modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.white, padding = padding),
            uiState = uiState,
            onUiAction = viewModel::onUiAction
        )

        is MeetingUiState.Error -> ErrorScreen(
            modifier = modifier,
            onClickRefresh = { viewModel.onUiAction(MeetingUiAction.OnClickRefresh) }
        )
    }
}

@Composable
fun MeetingScreen(
    modifier: Modifier = Modifier,
    uiState: MeetingUiState.Success,
    onUiAction: OnMeetingUiAction = {}
) {
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

            if (uiState.meetings.isNotEmpty()) {
                MeetingContent(
                    modifier = contentModifier,
                    meetings = uiState.meetings,
                    onUiAction = onUiAction
                )
            } else {
                MeetingEmptyScreen(
                    modifier =  contentModifier
                )
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
    meetings: List<Meeting>,
    onUiAction: OnMeetingUiAction
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 28.dp, horizontal = 20.dp)
    ) {
        items(
            items = meetings,
            key = { it.id },
        ) {
            MeetingCard(
                meeting = it,
                onUiAction = onUiAction
            )
        }
    }
}

@Composable
fun MeetingEmptyScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(80.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_group),
            contentDescription = "",
            tint = MoimTheme.colors.icon
        )

        Text(
            text = stringResource(R.string.meeting_new_meeting),
            style = MoimTheme.typography.body01.medium,
            color = MoimTheme.colors.gray.gray06
        )
    }
}

@Preview
@Composable
private fun MeetingScreenPreview() {
    MoimTheme {
        MeetingScreen(
            uiState = MeetingUiState.Success(meetings = emptyList())
        )
    }
}