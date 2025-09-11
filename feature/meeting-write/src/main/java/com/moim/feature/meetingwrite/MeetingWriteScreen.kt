package com.moim.feature.meetingwrite

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.common.view.ObserveAsEvents
import com.moim.core.common.view.showToast
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.component.MoimPrimaryButton
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.meetingwrite.ui.MeetingWriteImage
import com.moim.feature.meetingwrite.ui.MeetingWriteImageEditDialog
import com.moim.feature.meetingwrite.ui.MeetingWriteNameTextField

internal typealias OnMeetingWriteUiAction = (MeetingWriteUiAction) -> Unit

@Composable
fun MeetingWriteRoute(
    viewModel: MeetingWriteViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToBack: () -> Unit
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val meetingWriteUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) viewModel.onUiAction(MeetingWriteUiAction.OnChangeMeetingPhotoUrl(uri.toString())) }
    )

    val modifier = Modifier.containerScreen(
        backgroundColor = MoimTheme.colors.white,
        padding = padding
    )

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is MeetingWriteUiEvent.NavigateToBack -> navigateToBack()
            is MeetingWriteUiEvent.NavigateToPhotoPicker -> singlePhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            is MeetingWriteUiEvent.ShowToastMessage -> showToast(context, event.message)
        }
    }

    when (val uiState = meetingWriteUiState) {
        is MeetingWriteUiState.MeetingWrite -> MeetingWriteScreen(
            modifier = modifier,
            uiState = uiState,
            isLoading = isLoading,
            onUiAction = viewModel::onUiAction
        )
    }
}

@Composable
fun MeetingWriteScreen(
    modifier: Modifier = Modifier,
    uiState: MeetingWriteUiState.MeetingWrite,
    isLoading: Boolean,
    onUiAction: OnMeetingWriteUiAction,
) {
    TrackScreenViewEvent(screenName = "meet_write")
    Column(
        modifier = modifier
    ) {
        MoimTopAppbar(
            title = stringResource(if (uiState.meetingId.isNullOrEmpty()) R.string.meeting_write_title_for_create else R.string.meeting_write_title_for_update),
            onClickNavigate = { onUiAction(MeetingWriteUiAction.OnClickBack) }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
                .imePadding()
        ) {
            MeetingWriteImage(
                meetingImageUrl = uiState.meetingUrl,
                onUiAction = onUiAction
            )

            MeetingWriteNameTextField(
                meetingName = uiState.meetingName,
                onUiAction = onUiAction
            )

            Spacer(Modifier.weight(1f))

            MoimPrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                enable = uiState.enableMeetingWrite,
                text = stringResource(if (uiState.meetingId.isNullOrEmpty()) R.string.meeting_write_create else R.string.common_save),
                onClick = { onUiAction(MeetingWriteUiAction.OnClickMeetingWrite) },
            )
        }
    }

    if (uiState.isShowPhotoEditDialog) {
        MeetingWriteImageEditDialog(onUiAction = onUiAction)
    }

    LoadingDialog(isLoading)
}