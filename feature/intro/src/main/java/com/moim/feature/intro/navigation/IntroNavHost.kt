package com.moim.feature.intro.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.moim.core.ui.route.IntroRoute
import com.moim.feature.intro.screen.signin.SignInRoute
import com.moim.feature.intro.screen.signup.SignUpRoute
import com.moim.feature.intro.screen.signup.SignUpViewModel
import com.moim.feature.intro.screen.splash.SplashRoute

@Composable
fun IntroNavHost(navigateToMain: () -> Unit) {
    val backStack = remember { mutableStateListOf<IntroRoute>(IntroRoute.Splash) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is IntroRoute.Splash -> {
                    NavEntry(key) {
                        SplashRoute(
                            navigateToSignIn = {
                                backStack.clear()
                                backStack.add(IntroRoute.SignIn)
                            },
                            navigateToMain = { navigateToMain() },
                        )
                    }
                }

                is IntroRoute.SignIn -> {
                    NavEntry(key) {
                        SignInRoute(
                            navigateToSignUp = { email, token ->
                                backStack.clear()
                                backStack.add(IntroRoute.SignUp(email, token))
                            },
                            navigateToMain = {
                                navigateToMain()
                            },
                        )
                    }
                }

                is IntroRoute.SignUp -> {
                    NavEntry(key) {
                        SignUpRoute(
                            navigateToMain = {
                                navigateToMain()
                            },
                            viewModel =
                                hiltViewModel<SignUpViewModel, SignUpViewModel.Factory>(
                                    key = key.token,
                                ) { factory ->
                                    factory.create(key)
                                },
                        )
                    }
                }
            }
        },
    )
}
