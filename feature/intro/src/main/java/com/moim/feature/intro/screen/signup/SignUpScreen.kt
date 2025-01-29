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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.common.view.ObserveAsEvents
import com.moim.core.common.view.showToast
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.component.MoimPrimaryButton
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.intro.screen.signup.ui.NicknameTextField
import com.moim.feature.intro.screen.signup.ui.ProfileImage
import com.moim.feature.intro.screen.signup.ui.ProfileImageEditDialog

internal typealias OnSignUpUiAction = (SignUpUiAction) -> Unit

@Composable
fun SignUpRoute(
    viewModel: SignUpViewModel = hiltViewModel(),
    navigateToMain: () -> Unit
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val signUpUiState by viewModel.uiState.collectAsStateWithLifecycle()

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) viewModel.onUiAction(SignUpUiAction.OnChangeProfileUrl(uri.toString())) }
    )

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is SignUpUiEvent.NavigateToPhotoPicker -> singlePhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            is SignUpUiEvent.NavigateToMain -> navigateToMain()
            is SignUpUiEvent.ShowToastMessage -> showToast(context, event.messageRes)
        }
    }

    when (val uiState = signUpUiState) {
        is SignUpUiState.SignUp -> SignUpScreen(
            modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.white),
            uiState = uiState,
            isLoading = isLoading,
            onUiAction = viewModel::onUiAction
        )
    }
}

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    uiState: SignUpUiState.SignUp = SignUpUiState.SignUp(),
    isLoading: Boolean = false,
    onUiAction: OnSignUpUiAction = {}
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 28.dp),
    ) {
        MoimText(
            text = stringResource(R.string.sign_up_title),
            singleLine = false,
            style = MoimTheme.typography.heading.bold,
            color = MoimTheme.colors.gray.gray01
        )

        ProfileImage(
            profileUrl = uiState.profileUrl,
            onUiAction = onUiAction
        )

        NicknameTextField(
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
            enable = uiState.enableSignUp,
            text = stringResource(R.string.sign_up_start),
            onClick = { onUiAction(SignUpUiAction.OnClickSignUp) },
        )
    }

    if (uiState.isShowProfileEditDialog) {
        ProfileImageEditDialog(onUiAction = onUiAction)
    }

    LoadingDialog(isLoading)
}

@Preview
@Composable
private fun SignUpScreenPreview() {
    MoimTheme {
        SignUpScreen(
            modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.white),
            uiState = SignUpUiState.SignUp(enableSignUp = true)
        )
    }
}