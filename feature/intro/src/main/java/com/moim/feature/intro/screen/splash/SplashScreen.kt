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
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.designsystem.R
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.component.MoimAlertDialog
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.view.showToast
import com.moim.feature.intro.screen.splash.model.SplashIntent
import com.moim.feature.intro.screen.splash.model.SplashSideEffect
import com.moim.feature.intro.screen.splash.model.SplashState
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SplashRoute(
    viewModel: SplashViewModel = hiltViewModel(),
    navigateToSignIn: () -> Unit,
    navigateToMain: () -> Unit,
) {
    val splashUiState by viewModel.collectAsState()
    val activity = LocalContext.current as Activity

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SplashSideEffect.NavigateToSignIn -> {
                navigateToSignIn()
            }

            is SplashSideEffect.NavigateToMain -> {
                navigateToMain()
            }

            is SplashSideEffect.NavigateToExit -> {
                activity.finish()
            }

            is SplashSideEffect.NavigateToPlayStore -> {
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

    SplashScreen(
        uiState = splashUiState,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun SplashScreen(
    uiState: SplashState,
    onIntent: (SplashIntent) -> Unit,
) {
    TrackScreenViewEvent(screenName = "splash")

    val iconRes =
        if (MoimTheme.isDarkTheme) {
            R.drawable.ic_logo_full_dark
        } else {
            R.drawable.ic_logo_full_light
        }

    Box(
        modifier =
            Modifier
                .containerScreen(backgroundColor = MoimTheme.colors.bg.primary)
                .systemBarsPadding(),
    ) {
        Icon(
            modifier = Modifier.align(Alignment.Center),
            imageVector = ImageVector.vectorResource(iconRes),
            contentDescription = "",
            tint = Color.Unspecified,
        )
    }

    if (uiState.isShowErrorDialog) {
        MoimAlertDialog(
            title = stringResource(R.string.common_error),
            description = stringResource(R.string.common_error_disconnection),
            isNegative = false,
            cancelable = false,
            positiveText = stringResource(R.string.common_confirm),
            onClickPositive = { onIntent(SplashIntent.ExitClick) },
        )
    }

    if (uiState.isShowForceUpdateDialog) {
        MoimAlertDialog(
            title = stringResource(R.string.splash_force_update_title),
            description = stringResource(R.string.splash_force_update_description),
            isNegative = false,
            cancelable = false,
            positiveText = stringResource(R.string.common_confirm),
            onClickPositive = { onIntent(SplashIntent.ForceUpdateClick) },
        )
    }
}

@ThemePreviews
@Composable
private fun SplashScreenPreview() {
    SplashScreen(
        uiState = SplashState(isShowForceUpdateDialog = true),
        onIntent = {},
    )
}
