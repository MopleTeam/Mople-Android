package com.moim.feature.intro.screen.signup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
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
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.component.MoimPrimaryButton
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.view.showToast
import com.moim.feature.intro.screen.signup.model.SignUpIntent
import com.moim.feature.intro.screen.signup.model.SignUpSideEffect
import com.moim.feature.intro.screen.signup.model.SignUpState
import com.moim.feature.intro.screen.signup.ui.NicknameTextField
import com.moim.feature.intro.screen.signup.ui.ProfileImage
import com.moim.feature.intro.screen.signup.ui.ProfileImageEditDialog
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

internal typealias OnSignUpIntent = (SignUpIntent) -> Unit

@Composable
fun SignUpRoute(
    viewModel: SignUpViewModel = hiltViewModel(),
    navigateToMain: () -> Unit,
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val signUpUiState by viewModel.collectAsState()

    val singlePhotoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri -> if (uri != null) viewModel.onIntent(SignUpIntent.ProfileUrlChange(uri.toString())) },
        )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SignUpSideEffect.NavigateToPhotoPicker -> {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            }

            is SignUpSideEffect.NavigateToMain -> {
                navigateToMain()
            }

            is SignUpSideEffect.ShowToastMessage -> {
                showToast(context, sideEffect.message)
            }
        }
    }

    SignUpScreen(
        modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.bg.primary),
        uiState = signUpUiState,
        isLoading = isLoading,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    uiState: SignUpState = SignUpState(),
    isLoading: Boolean = false,
    onIntent: OnSignUpIntent = {},
) {
    TrackScreenViewEvent(screenName = "sign_up")
    Column(
        modifier =
            modifier
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 28.dp),
    ) {
        MoimText(
            text = stringResource(R.string.sign_up_title),
            singleLine = false,
            style = MoimTheme.typography.heading.bold,
            color = MoimTheme.colors.text.text01,
        )

        ProfileImage(
            profileUrl = uiState.profileUrl,
            onIntent = onIntent,
        )

        NicknameTextField(
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
            enable = uiState.enableSignUp,
            text = stringResource(R.string.sign_up_start),
            onClick = { onIntent(SignUpIntent.SignUpClick) },
        )
    }

    if (uiState.isShowProfileEditDialog) {
        ProfileImageEditDialog(onIntent = onIntent)
    }

    LoadingDialog(isLoading)
}

@Preview
@Composable
private fun SignUpScreenPreview() {
    MoimTheme {
        SignUpScreen(
            modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.bg.primary),
            uiState = SignUpState(enableSignUp = true),
        )
    }
}
