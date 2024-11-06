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
        SignUpRoute(navigateToMain = navigateToMain)
    }
}

fun NavController.navigateToSignUp(
    email: String,
    token: String,
    navOptions: NavOptions? = null
) {
    this.navigate(IntroRoute.SignUp(email, token), navOptions)
}