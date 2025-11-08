package com.moim.feature.intro.screen.signin

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.ui.route.IntroRoute

fun NavGraphBuilder.signInNavigation(
    navigateToSignUp: (String, String, NavOptions) -> Unit,
    navigateToMain: () -> Unit,
) {
    composable<IntroRoute.SignIn> {
        SignInRoute(
            navigateToSignUp = navigateToSignUp,
            navigateToMain = navigateToMain
        )
    }
}

fun NavController.navigateToSignIn(navOptions: NavOptions? = null) {
    this.navigate(IntroRoute.SignIn, navOptions)
}