package com.moim.feature.meetingsetting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.common.view.ObserveAsEvents
import com.moim.core.common.view.showToast
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.component.MoimAlertDialog
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.moimButtomColors
import com.moim.core.model.Meeting
import com.moim.feature.meetingsetting.ui.MeetingSettingParticipantsInfo
import com.moim.feature.meetingsetting.ui.MeetingSettingProfile
import com.moim.feature.meetingsetting.ui.MeetingSettingTopAppbar

internal typealias OnMeetingSettingUiAction = (MeetingSettingUiAction) -> Unit

@Composable
fun MeetingSettingRoute(
    viewModel: MeetingSettingViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToBack: (Boolean) -> Unit,
    navigateToParticipants: (isMeeting: Boolean, isPlan: Boolean, id: String) -> Unit,
    navigateToMeetingWrite: (Meeting) -> Unit,
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val meetingUiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is MeetingSettingUiEvent.NavigateToBack -> navigateToBack(false)
            is MeetingSettingUiEvent.NavigateToBackForDelete -> navigateToBack(true)
            is MeetingSettingUiEvent.NavigateToMeetingWrite -> navigateToMeetingWrite(event.meeting)
            is MeetingSettingUiEvent.NavigateToMeetingParticipants -> navigateToParticipants(true, false, event.meetingId)
            is MeetingSettingUiEvent.ShowToastMessage -> showToast(context, event.message)
        }
    }

    when (val uiState = meetingUiState) {
        is MeetingSettingUiState.MeetingSetting -> MeetingSettingScreen(
            modifier = Modifier.containerScreen(padding, MoimTheme.colors.white),
            uiState = uiState,
            isLoading = isLoading,
            onUiAction = viewModel::onUiAction
        )
    }
}

@Composable
fun MeetingSettingScreen(
    modifier: Modifier = Modifier,
    uiState: MeetingSettingUiState.MeetingSetting,
    isLoading: Boolean,
    onUiAction: OnMeetingSettingUiAction
) {
    TrackScreenViewEvent(screenName = "meet_setting")
    Column(
        modifier = modifier,
    ) {
        MeetingSettingTopAppbar(onUiAction = onUiAction)
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            MeetingSettingProfile(
                meeting = uiState.meeting,
                isMeetingHost = uiState.isHostUser,
                onUiAction = onUiAction,
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(MoimTheme.colors.stroke)
            )
            MeetingSettingParticipantsInfo(
                meeting = uiState.meeting,
                onUiAction = onUiAction
            )
            MeetingSettingExit(
                isHostUser = uiState.isHostUser,
                onUiAction = onUiAction
            )
        }
    }

    if (uiState.isShowMeetingExitDialog) {
        MeetingExitDialog(
            isDelete = false,
            dismissAction = MeetingSettingUiAction.OnShowMeetingExitDialog(false),
            onUiAction = onUiAction
        )
    }

    if (uiState.isShowMeetingDeleteDialog) {
        MeetingExitDialog(
            isDelete = true,
            dismissAction = MeetingSettingUiAction.OnShowMeetingDeleteDialog(false),
            onUiAction = onUiAction
        )
    }

    LoadingDialog(isShow = isLoading)
}

@Composable
fun MeetingExitDialog(
    isDelete: Boolean,
    dismissAction: MeetingSettingUiAction,
    onUiAction: OnMeetingSettingUiAction,
) {
    MoimAlertDialog(
        title = stringResource(R.string.meeting_setting_exit),
        description = stringResource(R.string.meeting_setting_exit_description),
        positiveText = stringResource(if (isDelete) R.string.meeting_setting_delete_btn else R.string.meeting_setting_exit_btn),
        positiveButtonColors = moimButtomColors().copy(containerColor = MoimTheme.colors.secondary),
        onClickPositive = {
            onUiAction(dismissAction)
            onUiAction(MeetingSettingUiAction.OnClickMeetingExit)
        },
        onClickNegative = { onUiAction(dismissAction) },
        onDismiss = { onUiAction(dismissAction) }
    )
}

@Composable
fun MeetingSettingExit(
    isHostUser: Boolean,
    onUiAction: OnMeetingSettingUiAction
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .onSingleClick {
                if (isHostUser) {
                    onUiAction(MeetingSettingUiAction.OnShowMeetingDeleteDialog(true))
                } else {
                    onUiAction(MeetingSettingUiAction.OnShowMeetingExitDialog(true))
                }
            }
            .padding(vertical = 16.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MoimText(
            modifier = Modifier.weight(1f),
            text = stringResource(if (isHostUser) R.string.meeting_setting_delete_btn else R.string.meeting_setting_exit),
            style = MoimTheme.typography.title03.medium,
            color = if (isHostUser) MoimTheme.colors.red else MoimTheme.colors.gray.gray01
        )
    }
}

@Preview
@Composable
private fun MeetingSettingScreenPreview() {
    MoimTheme {
        MeetingSettingScreen(
            modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.white),
            uiState = MeetingSettingUiState.MeetingSetting(
                meeting = Meeting(
                    name = "우리중학교 동창",
                    sinceDays = 12,
                ),
                isHostUser = true
            ),
            isLoading = false,
            onUiAction = {}
        )
    }
}