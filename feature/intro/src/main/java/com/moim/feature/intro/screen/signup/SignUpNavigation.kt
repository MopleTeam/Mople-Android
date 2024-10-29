package com.moim.feature.intro.screen.signup

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.route.IntroRoute

fun NavGraphBuilder.signUpNavigation(
    navigateToMain: () -> Unit
) {
    composable<IntroRoute.SignUp> {
        SignUpScreen(navigateToMain = navigateToMain)
    }
}

fun NavController.navigateToSignUp(navOptions: NavOptions? = null) {
    this.navigate(IntroRoute.SignUp, navOptions)
}