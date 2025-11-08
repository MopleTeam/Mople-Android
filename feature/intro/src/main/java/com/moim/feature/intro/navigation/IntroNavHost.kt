package com.moim.feature.intro.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.route.IntroRoute
import com.moim.feature.intro.screen.signin.navigateToSignIn
import com.moim.feature.intro.screen.signin.signInNavigation
import com.moim.feature.intro.screen.signup.navigateToSignUp
import com.moim.feature.intro.screen.signup.signUpNavigation
import com.moim.feature.intro.screen.splash.splashNavigation


@Composable
fun IntroNavHost(
    modifier: Modifier = Modifier,
    startDestination: IntroRoute = IntroRoute.Splash,
    navigateToMain: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier
            .fillMaxSize()
            .background(MoimTheme.colors.white),
        navController = navController,
        startDestination = startDestination,
    ) {
        splashNavigation(
            navigateToSignIn = navController::navigateToSignIn,
            navigateToMain = navigateToMain
        )

        signInNavigation(
            navigateToSignUp = navController::navigateToSignUp,
            navigateToMain = navigateToMain
        )

        signUpNavigation(
            navigateToMain = navigateToMain
        )
    }
}