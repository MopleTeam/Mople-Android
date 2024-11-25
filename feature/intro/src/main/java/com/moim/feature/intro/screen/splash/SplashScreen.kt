package com.moim.feature.intro.screen.splash

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavOptions
import com.moim.core.common.view.ObserveAsEvents
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimAlertDialog
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.route.IntroRoute

@Composable
fun SplashRoute(
    viewModel: SplashViewModel = hiltViewModel(),
    navigateToSignIn: (NavOptions) -> Unit,
    navigateToMain: () -> Unit,
) {
    val splashUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as Activity
    val options = NavOptions.Builder()
        .setPopUpTo(IntroRoute.Splash, inclusive = true)
        .build()

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is SplashUiEvent.NavigateToSignIn -> navigateToSignIn(options)
            is SplashUiEvent.NavigateToMain -> navigateToMain()
        }
    }

    BackHandler {}

    when (val uiState = splashUiState) {
        is SplashUiState.Splash -> SplashScreen(
            uiState = uiState,
            onClickFinish = activity::finish
        )
    }
}

@Composable
private fun SplashScreen(
    uiState: SplashUiState.Splash,
    onClickFinish: () -> Unit
) {
    Box(
        modifier = Modifier
            .containerScreen(backgroundColor = MoimTheme.colors.white)
            .systemBarsPadding()
    ) {
        Icon(
            modifier = Modifier.align(Alignment.Center),
            imageVector = ImageVector.vectorResource(R.drawable.ic_logo_full),
            contentDescription = "",
            tint = Color.Unspecified
        )
    }

    if (uiState.isShowErrorDialog) {
        MoimAlertDialog(
            title = stringResource(R.string.common_error),
            description = stringResource(R.string.common_error_description),
            isNegative = false,
            cancelable = false,
            positiveText = stringResource(R.string.common_confirm),
            onClickPositive = onClickFinish
        )
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    SplashScreen(
        uiState = SplashUiState.Splash(isShowErrorDialog = false),
        onClickFinish = {}
    )
}