package com.moim.feature.intro.screen.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavOptions
import com.moim.core.common.view.ObserveAsEvents
import com.moim.core.common.view.showToast
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.component.MoimPrimaryButton
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.color_222222
import com.moim.core.designsystem.theme.color_3366FF
import com.moim.core.designsystem.theme.color_7A7A7A
import com.moim.core.designsystem.theme.color_FEE500
import com.moim.core.designsystem.theme.color_FFFFFF
import com.moim.core.designsystem.theme.moimButtomColors
import com.moim.core.route.IntroRoute

internal typealias OnSignInUiAction = (SignInUiAction) -> Unit

@Composable
fun SignInRoute(
    viewModel: SignInViewModel = hiltViewModel(),
    navigateToSignUp: (String, String, NavOptions) -> Unit,
    navigateToMain: () -> Unit,
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val options = NavOptions.Builder()
        .setPopUpTo(IntroRoute.SignIn, inclusive = true)
        .build()

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is SignInUiEvent.NavigateToSignUp -> navigateToSignUp(event.email, event.token, options)
            is SignInUiEvent.NavigateToMain -> navigateToMain()
            is SignInUiEvent.ShowToastMessage -> showToast(context, event.messageRes)
        }
    }

    SignInScreen(
        isLoading = isLoading,
        onUiAction = viewModel::onUiAction
    )
}

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onUiAction: OnSignInUiAction,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color_FFFFFF)
            .systemBarsPadding()
            .padding(horizontal = 20.dp, vertical = 28.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MoimTheme.typography.heading.bold,
                color = color_3366FF
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.app_description),
                style = MoimTheme.typography.body01.regular,
                color = color_7A7A7A
            )
        }

        KakaoLoginButton(onUiAction = onUiAction)
    }

    LoadingDialog(isShow = isLoading)
}

@Composable
private fun BoxScope.KakaoLoginButton(
    onUiAction: OnSignInUiAction
) {
    val context = LocalContext.current

    MoimPrimaryButton(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
        buttonColors = moimButtomColors().copy(
            containerColor = color_FEE500,
            contentColor = color_222222
        ),
        onClick = { onUiAction(SignInUiAction.OnClickKakaoLogin(context)) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_kakao),
                contentDescription = ""
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.sign_in_kakao),
                style = MoimTheme.typography.body01.semiBold
            )
        }
    }
}

@Preview
@Composable
private fun SignInScreenPreview() {
    MoimTheme {
        SignInScreen { }
    }
}