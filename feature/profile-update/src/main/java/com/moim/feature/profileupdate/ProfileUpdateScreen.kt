package com.moim.feature.profileupdate

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.common.view.ObserveAsEvents
import com.moim.core.common.view.showToast
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.component.MoimPrimaryButton
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.profileupdate.ui.ProfileUpdateImage
import com.moim.feature.profileupdate.ui.ProfileUpdateImageEditDialog
import com.moim.feature.profileupdate.ui.ProfileUpdateNicknameTextField

internal typealias OnProfileUpdateUiAction = (ProfileUpdateUiAction) -> Unit

@Composable
fun ProfileUpdateRoute(
    viewModel: ProfileUpdateViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToBack: () -> Unit,
) {
    val context = LocalContext.current
    val profileUpdateUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) viewModel.onUiAction(ProfileUpdateUiAction.OnChangeProfileUrl(uri.toString())) }
    )
    val modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.white, padding = padding)

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is ProfileUpdateUiEvent.NavigateToBack -> navigateToBack()
            is ProfileUpdateUiEvent.NavigateToPhotoPicker -> singlePhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            is ProfileUpdateUiEvent.ShowToastMessage -> showToast(context, event.messageRes)
        }
    }

    when (val uiState = profileUpdateUiState) {
        is ProfileUpdateUiState.Loading -> LoadingScreen(modifier)

        is ProfileUpdateUiState.Success -> ProfileUpdateScreen(
            modifier = modifier,
            uiState = uiState,
            isLoading = isLoading,
            onUiAction = viewModel::onUiAction
        )

        is ProfileUpdateUiState.Error -> ErrorScreen(
            modifier = modifier,
            onClickRefresh = { viewModel.onUiAction(ProfileUpdateUiAction.OnClickRefresh) }
        )
    }
}

@Composable
fun ProfileUpdateScreen(
    modifier: Modifier = Modifier,
    uiState: ProfileUpdateUiState.Success,
    isLoading: Boolean,
    onUiAction: OnProfileUpdateUiAction
) {
    Column(
        modifier = modifier
    ) {
        MoimTopAppbar(
            title = stringResource(R.string.profile_update_title),
            onClickNavigate = { onUiAction(ProfileUpdateUiAction.OnClickBack) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
                .imePadding()
        ) {
            ProfileUpdateImage(
                profileUrl = uiState.profileUrl,
                onUiAction = onUiAction
            )

            ProfileUpdateNicknameTextField(
                nickname = uiState.nickname,
                isDuplicated = uiState.isDuplicatedName,
                isRegexError = uiState.isRegexError,
                onUiAction = onUiAction
            )

            Spacer(Modifier.weight(1f))

            MoimPrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                enable = uiState.enableProfileUpdate,
                text = stringResource(R.string.common_save),
                onClick = { onUiAction(ProfileUpdateUiAction.OnClickProfileUpdate) },
            )
        }
    }
    if (uiState.isShowProfileEditDialog) {
        ProfileUpdateImageEditDialog(onUiAction = onUiAction)
    }

    LoadingDialog(isLoading)
}

@Preview
@Composable
private fun ProfileUpdateScreenPreview() {
    MoimTheme {
        ProfileUpdateScreen(
            modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.white),
            uiState = ProfileUpdateUiState.Success(),
            isLoading = false,
            onUiAction = {}
        )
    }
}