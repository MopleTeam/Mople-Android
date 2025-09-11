package com.moim.feature.intro.screen.splash

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
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
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavOptions
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.common.view.ObserveAsEvents
import com.moim.core.common.view.showToast
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimAlertDialog
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.common.route.IntroRoute

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
            is SplashUiEvent.NavigateToExit -> activity.finish()
            is SplashUiEvent.NavigateToPlayStore -> {
                val packageName = activity.packageName.replace(".dev", "")
                try {
                    activity.startActivity(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=$packageName".toUri()))
                } catch (e: ActivityNotFoundException) {
                    showToast(activity, activity.getString(R.string.common_error_open_browser))
                    activity.finish()
                }
            }
        }
    }

    BackHandler {}

    when (val uiState = splashUiState) {
        is SplashUiState.Splash -> SplashScreen(
            uiState = uiState,
            onUiAction = viewModel::onUiAction
        )
    }
}

@Composable
private fun SplashScreen(
    uiState: SplashUiState.Splash,
    onUiAction: (SplashUiAction) -> Unit
) {
    TrackScreenViewEvent(screenName = "splash")
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
            description = stringResource(R.string.common_error_disconnection),
            isNegative = false,
            cancelable = false,
            positiveText = stringResource(R.string.common_confirm),
            onClickPositive = { onUiAction(SplashUiAction.OnClickExit) }
        )
    }

    if (uiState.isShowForceUpdateDialog) {
        MoimAlertDialog(
            title = stringResource(R.string.splash_force_update_title),
            description = stringResource(R.string.splash_force_update_description),
            isNegative = false,
            cancelable = false,
            positiveText = stringResource(R.string.common_confirm),
            onClickPositive = { onUiAction(SplashUiAction.OnClickForceUpdate) }
        )
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    SplashScreen(
        uiState = SplashUiState.Splash(isShowForceUpdateDialog = true),
        onUiAction = {}
    )
}