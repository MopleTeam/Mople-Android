package com.moim.feature.intro.screen.splash

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.route.IntroRoute

fun NavGraphBuilder.splashNavigation(
    navigateToSignIn: (NavOptions) -> Unit,
    navigateToMain: () -> Unit,
) {
    composable<IntroRoute.Splash> {
        SplashScreen(
            navigateToSignIn = navigateToSignIn,
            navigateToMain = navigateToMain
        )
    }
}

fun NavController.navigateToSplash(navOptions: NavOptions? = null) {
    this.navigate(IntroRoute.Splash, navOptions)
}