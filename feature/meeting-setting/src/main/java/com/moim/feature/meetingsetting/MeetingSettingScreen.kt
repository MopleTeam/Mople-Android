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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.common.model.Meeting
import com.moim.core.common.model.ViewIdType
import com.moim.core.designsystem.R
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.component.MoimAlertDialog
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.moimButtomColors
import com.moim.core.ui.view.showToast
import com.moim.feature.meetingsetting.model.MeetingSettingIntent
import com.moim.feature.meetingsetting.model.MeetingSettingSideEffect
import com.moim.feature.meetingsetting.model.MeetingSettingState
import com.moim.feature.meetingsetting.ui.MeetingSettingLeaderChange
import com.moim.feature.meetingsetting.ui.MeetingSettingParticipantsInfo
import com.moim.feature.meetingsetting.ui.MeetingSettingProfile
import com.moim.feature.meetingsetting.ui.MeetingSettingTopAppbar
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

internal typealias OnMeetingSettingIntent = (MeetingSettingIntent) -> Unit

@Composable
fun MeetingSettingRoute(
    viewModel: MeetingSettingViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToBack: (Boolean) -> Unit,
    navigateToParticipants: (ViewIdType) -> Unit,
    navigateToParticipantsForLeaderChange: (ViewIdType.MeetId) -> Unit,
    navigateToMeetingWrite: (Meeting) -> Unit,
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val uiState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MeetingSettingSideEffect.NavigateToBack -> {
                navigateToBack(false)
            }

            is MeetingSettingSideEffect.NavigateToBackForDelete -> {
                navigateToBack(true)
            }

            is MeetingSettingSideEffect.NavigateToMeetingWrite -> {
                navigateToMeetingWrite(sideEffect.meeting)
            }

            is MeetingSettingSideEffect.NavigateToMeetingParticipants -> {
                navigateToParticipants(sideEffect.viewIdType)
            }

            is MeetingSettingSideEffect.NavigateToParticipantsForLeaderChange -> {
                navigateToParticipantsForLeaderChange(sideEffect.viewIdType)
            }

            is MeetingSettingSideEffect.ShowToastMessage -> {
                showToast(context, sideEffect.message)
            }
        }
    }

    MeetingSettingScreen(
        modifier = Modifier.containerScreen(padding, MoimTheme.colors.bg.primary),
        uiState = uiState,
        isLoading = isLoading,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun MeetingSettingScreen(
    modifier: Modifier = Modifier,
    uiState: MeetingSettingState,
    isLoading: Boolean,
    onIntent: OnMeetingSettingIntent,
) {
    TrackScreenViewEvent(screenName = "meet_setting")
    Column(
        modifier = modifier,
    ) {
        MeetingSettingTopAppbar(onIntent = onIntent)
        Column(
            modifier =
                Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize(),
        ) {
            MeetingSettingProfile(
                meeting = uiState.meeting,
                isMeetingHost = uiState.isHostUser,
                onIntent = onIntent,
            )
            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(MoimTheme.colors.stroke),
            )
            MeetingSettingParticipantsInfo(
                meeting = uiState.meeting,
                onIntent = onIntent,
            )

            if (uiState.isHostUser) {
                MeetingSettingLeaderChange(
                    meetingId = uiState.meeting.id,
                    onIntent = onIntent,
                )
            }

            MeetingSettingExit(
                isHostUser = uiState.isHostUser,
                onIntent = onIntent,
            )
        }
    }

    if (uiState.isShowMeetingExitDialog) {
        MeetingExitDialog(
            dismissIntent = MeetingSettingIntent.MeetingExitDialogShow(false),
            onIntent = onIntent,
        )
    }

    if (uiState.isShowMeetingDeleteDialog) {
        val meetingIdType = ViewIdType.MeetId(uiState.meeting.id)
        MeetingDeleteDialog(
            dismissIntent = MeetingSettingIntent.MeetingDeleteDialogShow(false),
            meetingIdType = meetingIdType,
            onIntent = onIntent,
        )
    }

    LoadingDialog(isShow = isLoading)
}

@Composable
private fun MeetingExitDialog(
    dismissIntent: MeetingSettingIntent,
    onIntent: OnMeetingSettingIntent,
) {
    MoimAlertDialog(
        title = stringResource(R.string.meeting_setting_exit),
        description = stringResource(R.string.meeting_setting_exit_description),
        positiveText = stringResource(R.string.meeting_setting_exit_btn),
        positiveButtonColors = moimButtomColors().copy(containerColor = MoimTheme.colors.secondary),
        onClickPositive = {
            onIntent(dismissIntent)
            onIntent(MeetingSettingIntent.MeetingExitClick)
        },
        onClickNegative = { onIntent(dismissIntent) },
        onDismiss = { onIntent(dismissIntent) },
    )
}

@Composable
private fun MeetingDeleteDialog(
    meetingIdType: ViewIdType.MeetId,
    dismissIntent: MeetingSettingIntent,
    onIntent: OnMeetingSettingIntent,
) {
    MoimAlertDialog(
        title = stringResource(R.string.meeting_setting_delete_title),
        description = stringResource(R.string.meeting_setting_delete_description),
        negativeText = stringResource(R.string.meeting_setting_exit_btn),
        positiveText = stringResource(R.string.meeting_setting_participants_leader_change),
        positiveButtonColors = moimButtomColors().copy(containerColor = MoimTheme.colors.secondary),
        onClickPositive = {
            onIntent(dismissIntent)
            onIntent(MeetingSettingIntent.MeetingLeaderChangeClick(meetingIdType))
        },
        onClickNegative = {
            onIntent(dismissIntent)
            onIntent(MeetingSettingIntent.MeetingExitClick)
        },
        onDismiss = { onIntent(dismissIntent) },
    )
}

@Composable
private fun MeetingSettingExit(
    isHostUser: Boolean,
    onIntent: OnMeetingSettingIntent,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .onSingleClick {
                    if (isHostUser) {
                        onIntent(MeetingSettingIntent.MeetingDeleteDialogShow(true))
                    } else {
                        onIntent(MeetingSettingIntent.MeetingExitDialogShow(true))
                    }
                }.padding(vertical = 16.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MoimText(
            modifier = Modifier.weight(1f),
            text = stringResource(if (isHostUser) R.string.meeting_setting_delete_btn else R.string.meeting_setting_exit),
            style = MoimTheme.typography.title03.medium,
            color = if (isHostUser) MoimTheme.colors.global.red else MoimTheme.colors.text.text01,
        )
    }
}

@ThemePreviews
@Composable
private fun MeetingSettingScreenPreview() {
    MoimTheme {
        MeetingSettingScreen(
            modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.bg.primary),
            uiState =
                MeetingSettingState(
                    meeting =
                        Meeting(
                            name = "우리중학교 동창",
                            sinceDays = 12,
                        ),
                    isHostUser = true,
                    isShowMeetingDeleteDialog = false,
                ),
            isLoading = false,
            onIntent = {},
        )
    }
}
