package com.moim.feature.intro.screen.splash

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavOptions
import com.moim.core.common.view.ObserveAsEvents
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimAlertDialog
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.color_3366FF
import com.moim.core.designsystem.theme.color_FFFFFF
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
            .fillMaxSize()
            .background(color_FFFFFF)
            .systemBarsPadding()
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(R.string.app_name),
            style = MoimTheme.typography.heading.bold,
            color = color_3366FF
        )
    }

    if (uiState.isShowErrorDialog) {
        MoimAlertDialog(
            title = stringResource(R.string.common_error),
            description = stringResource(R.string.common_error_description),
            isNegative = false,
            cancelable = false,
            onClickPositive = onClickFinish
        )
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    SplashScreen(
        uiState = SplashUiState.Splash(isShowErrorDialog = true),
        onClickFinish = {}
    )
}