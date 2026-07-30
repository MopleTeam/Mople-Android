package com.moim.feature.meetingnoticewrite

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.designsystem.R
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.component.MoimPrimaryButton
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.component.MoimTextField
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.view.ObserveAsEvents
import com.moim.core.ui.view.showToast

@Composable
fun MeetingNoticeWriteRoute(
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    viewModel: MeetingNoticeWriteViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.bg.primary)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is MeetingNoticeWriteUiEvent.NavigateToBack -> navigateToBack()
            is MeetingNoticeWriteUiEvent.ShowToastMessage -> showToast(context, event.message)
        }
    }

    (uiState as? MeetingNoticeWriteUiState)?.let {
        MeetingNoticeWriteScreen(
            modifier = modifier,
            uiState = it,
            onUiAction = viewModel::onUiAction,
        )
    }
}

@Composable
private fun MeetingNoticeWriteScreen(
    modifier: Modifier = Modifier,
    uiState: MeetingNoticeWriteUiState,
    onUiAction: (MeetingNoticeWriteUiAction) -> Unit,
) {
    MoimScaffold(
        modifier =
            modifier
                .fillMaxSize()
                .imePadding(),
        topBar = {
            MoimTopAppbar(
                onClickNavigate = { onUiAction(MeetingNoticeWriteUiAction.OnClickBack) },
                title = stringResource(R.string.meeting_notice_write_title),
            )
        },
        content = { padding ->
            when (uiState) {
                is MeetingNoticeWriteUiState.Loading -> {
                    LoadingScreen(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(padding),
                    )
                }

                is MeetingNoticeWriteUiState.Success -> {
                    LaunchedEffect(uiState.noticeState.text) {
                        onUiAction(
                            MeetingNoticeWriteUiAction.OnChangeEnable(
                                isEnable = uiState.noticeState.text.isNotBlank(),
                            ),
                        )
                    }

                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(padding),
                    ) {
                        MoimTextField(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                                    .heightIn(min = 238.dp),
                            textFieldState = uiState.noticeState,
                            hintText = stringResource(R.string.meeting_notice_write_hint),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = false,
                        )
                    }
                }

                is MeetingNoticeWriteUiState.Error -> {
                    ErrorScreen(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(padding),
                        onClickRefresh = {
                            onUiAction(MeetingNoticeWriteUiAction.OnClickRefresh)
                        },
                    )
                }
            }
        },
        bottomBar = {
            if (uiState is MeetingNoticeWriteUiState.Success) {
                MoimPrimaryButton(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                    text = stringResource(R.string.meeting_notice_write_completed),
                    enable = uiState.enabled,
                    onClick = {
                        onUiAction(
                            MeetingNoticeWriteUiAction.OnClickConfirm(
                                meetId = uiState.meetId,
                                noticeId = uiState.noticeId,
                            ),
                        )
                    },
                )
            }
        },
    )
}

@ThemePreviews
@Composable
private fun MeetingNoticeWriteScreenPreview() {
    MoimTheme {
        MeetingNoticeWriteScreen(
            uiState = MeetingNoticeWriteUiState.Success(),
            onUiAction = {},
        )
    }
}
