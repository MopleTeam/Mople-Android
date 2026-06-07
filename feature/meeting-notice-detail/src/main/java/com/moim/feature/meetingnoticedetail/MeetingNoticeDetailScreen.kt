package com.moim.feature.meetingnoticedetail

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.view.ObserveAsEvents

@Composable
fun MeetingNoticeDetailRoute(
    viewModel: MeetingNoticeDetailViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToBack: () -> Unit,
) {
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.bg.primary)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is MeetingNoticeDetailUiEvent.NavigateToBack -> navigateToBack()
        }
    }

    (uiState as? MeetingNoticeDetailUiState)?.let {
        MeetingNoticeDetailScreen(
            modifier = modifier,
            uiState = it,
            onUiAction = viewModel::onUiAction,
        )
    }
}

@Composable
private fun MeetingNoticeDetailScreen(
    modifier: Modifier = Modifier,
    uiState: MeetingNoticeDetailUiState,
    onUiAction: (MeetingNoticeDetailUiAction) -> Unit,
) {
    MoimScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MoimTopAppbar(
                onClickNavigate = { onUiAction(MeetingNoticeDetailUiAction.OnClickBack) },
            )
        },
        content = {
            // TODO: content
        },
    )
}

@ThemePreviews
@Composable
private fun MeetingNoticeDetailScreenPreview() {
    MoimTheme {
        MeetingNoticeDetailScreen(
            uiState = MeetingNoticeDetailUiState(),
            onUiAction = {},
        )
    }
}
