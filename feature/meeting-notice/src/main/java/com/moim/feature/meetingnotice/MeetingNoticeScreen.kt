package com.moim.feature.meetingnotice

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.view.ObserveAsEvents

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

    when (val uiState = noticeUiState) {
        is MeetingNoticeUiState.Loading -> {
            LoadingScreen(modifier)
        }

        is MeetingNoticeUiState.Success -> {
            MeetingNoticeScreen(
                modifier = modifier,
                uiState = uiState,
                onUiAction = viewModel::onUiAction,
            )
        }

        is MeetingNoticeUiState.Error -> {
            ErrorScreen(modifier) {
                viewModel.onUiAction(MeetingNoticeUiAction.OnClickRefresh)
            }
        }
    }
}

@Composable
private fun MeetingNoticeScreen(
    modifier: Modifier = Modifier,
    uiState: MeetingNoticeUiState.Success,
    onUiAction: (MeetingNoticeUiAction) -> Unit,
) {
    MoimScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MoimTopAppbar(
                onClickNavigate = { onUiAction(MeetingNoticeUiAction.OnClickBack) },
            )
        },
        content = {
            // TODO: content
        },
    )
}

@ThemePreviews
@Composable
private fun MeetingNoticeScreenPreview() {
    MoimTheme {

    }
}
