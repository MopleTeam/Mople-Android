package com.moim.feature.participantlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import com.moim.feature.participantlist.ui.ParticipantListItem

@Composable
fun ParticipantListRoute(
    padding: PaddingValues,
    viewModel: ParticipantListViewModel = hiltViewModel(),
    navigateToBack: () -> Unit
) {
    val participantListUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modifier = Modifier.containerScreen(
        backgroundColor = MoimTheme.colors.white,
        padding = padding
    )

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is ParticipantListUiEvent.NavigateToBack -> navigateToBack()
        }
    }

    when (val uiState = participantListUiState) {
        is ParticipantListUiState.Loading -> LoadingScreen(modifier = modifier)

        is ParticipantListUiState.Success -> ParticipantListScreen(
            modifier = modifier,
            uiState = uiState,
            onUiAction = viewModel::onUiAction
        )

        is ParticipantListUiState.Error -> ErrorScreen(
            modifier = modifier,
            onClickRefresh = { viewModel.onUiAction(ParticipantListUiAction.OnClickRefresh) }
        )
    }
}

@Composable
fun ParticipantListScreen(
    modifier: Modifier = Modifier,
    uiState: ParticipantListUiState.Success,
    onUiAction: (ParticipantListUiAction) -> Unit
) {
    TrackScreenViewEvent(screenName = "participant_list")
    Column(
        modifier = modifier
    ) {
        MoimTopAppbar(
            title = stringResource(R.string.participant_list_title),
            onClickNavigate = { onUiAction(ParticipantListUiAction.OnClickBack) }
        )
        Spacer(Modifier.height(28.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MoimText(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.participant_list_title),
                style = MoimTheme.typography.body01.medium,
                color = MoimTheme.colors.gray.gray04
            )
            MoimText(
                text = stringResource(R.string.unit_participants_count_short, uiState.participant.size),
                style = MoimTheme.typography.body01.medium,
                color = MoimTheme.colors.gray.gray04
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(
                items = uiState.participant,
                key = { it.memberId }
            ) {
                ParticipantListItem(
                    isMeeting = uiState.isMeeting,
                    participant = it
                )
            }
        }
    }
}