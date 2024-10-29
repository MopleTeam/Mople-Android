package com.moim.feature.intro.screen.signin

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavOptions

@Composable
fun SignInScreen(
    signInViewModel: SignInViewModel = hiltViewModel(),
    navigateToSignUp: (NavOptions) -> Unit,
    navigateToMain: () -> Unit,
) {

}