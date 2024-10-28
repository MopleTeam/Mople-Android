package com.moim.core.route

import kotlinx.serialization.Serializable

sealed interface IntroRoute {

    @Serializable
    data object Splash : IntroRoute

    @Serializable
    data object SignIn : IntroRoute

    @Serializable
    data object SignUp : IntroRoute
}

sealed interface MainRoute {

}