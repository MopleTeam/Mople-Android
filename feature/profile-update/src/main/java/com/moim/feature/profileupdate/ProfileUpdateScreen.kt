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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.common.model.User
import com.moim.core.common.result.Result
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.component.MoimPrimaryButton
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.view.showToast
import com.moim.feature.profileupdate.model.ProfileUpdateIntent
import com.moim.feature.profileupdate.model.ProfileUpdateSideEffect
import com.moim.feature.profileupdate.model.ProfileUpdateState
import com.moim.feature.profileupdate.ui.ProfileUpdateImage
import com.moim.feature.profileupdate.ui.ProfileUpdateImageEditDialog
import com.moim.feature.profileupdate.ui.ProfileUpdateNicknameTextField
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

internal typealias OnProfileUpdateIntent = (ProfileUpdateIntent) -> Unit

@Composable
fun ProfileUpdateRoute(
    viewModel: ProfileUpdateViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToBack: () -> Unit,
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val uiState by viewModel.collectAsState()
    val modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.bg.primary, padding = padding)
    val singlePhotoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri -> if (uri != null) viewModel.onIntent(ProfileUpdateIntent.ProfileUrlChange(uri.toString())) },
        )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ProfileUpdateSideEffect.NavigateToBack -> {
                navigateToBack()
            }

            is ProfileUpdateSideEffect.NavigateToPhotoPicker -> {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            }

            is ProfileUpdateSideEffect.ShowToastMessage -> {
                showToast(context, sideEffect.message)
            }
        }
    }

    when {
        uiState.isLoading -> {
            LoadingScreen(modifier)
        }

        uiState.isSuccess -> {
            ProfileUpdateScreen(
                modifier = modifier,
                uiState = uiState,
                isLoading = isLoading,
                onIntent = viewModel::onIntent,
            )
        }

        uiState.isError -> {
            ErrorScreen(
                modifier = modifier,
                onClickRefresh = { viewModel.onIntent(ProfileUpdateIntent.RefreshClick) },
            )
        }
    }
}

@Composable
fun ProfileUpdateScreen(
    modifier: Modifier = Modifier,
    uiState: ProfileUpdateState,
    isLoading: Boolean,
    onIntent: OnProfileUpdateIntent,
) {
    TrackScreenViewEvent(screenName = "profile_write")
    Column(
        modifier = modifier,
    ) {
        MoimTopAppbar(
            title = stringResource(R.string.profile_update_title),
            onClickNavigate = { onIntent(ProfileUpdateIntent.BackClick) },
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp)
                    .imePadding(),
        ) {
            ProfileUpdateImage(
                profileUrl = uiState.profileUrl,
                onIntent = onIntent,
            )

            ProfileUpdateNicknameTextField(
                nickname = uiState.nickname,
                isDuplicated = uiState.isDuplicatedName,
                isRegexError = uiState.isRegexError,
                onIntent = onIntent,
            )

            Spacer(Modifier.weight(1f))

            MoimPrimaryButton(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 28.dp),
                enable = uiState.enableProfileUpdate,
                text = stringResource(R.string.common_save),
                onClick = { onIntent(ProfileUpdateIntent.ProfileUpdateClick) },
            )
        }
    }
    if (uiState.isShowProfileEditDialog) {
        ProfileUpdateImageEditDialog(onIntent = onIntent)
    }

    LoadingDialog(isLoading)
}

@Preview
@Composable
private fun ProfileUpdateScreenPreview() {
    MoimTheme {
        ProfileUpdateScreen(
            modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.bg.primary),
            uiState = ProfileUpdateState(user = Result.Success(User(userId = ""))),
            isLoading = false,
            onIntent = {},
        )
    }
}
