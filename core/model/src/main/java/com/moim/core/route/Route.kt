package com.moim.core.route

import kotlinx.serialization.Serializable


sealed interface Route

sealed interface IntroRoute : Route {

    @Serializable
    data object Splash : IntroRoute

    @Serializable
    data object SignIn : IntroRoute

    @Serializable
    data class SignUp(
        val email: String,
        val token: String
    ) : IntroRoute
}

sealed interface MainRoute : Route {

    @Serializable
    data object Home : MainRoute

    @Serializable
    data object Meeting : MainRoute

    @Serializable
    data object Calendar : MainRoute

    @Serializable
    data object Profile : MainRoute
}

sealed interface DetailRoute : Route {

    @Serializable
    data class MeetingWrite(val meetingId: String? = null) : DetailRoute

    @Serializable
    data object MeetingDetail : DetailRoute

    @Serializable
    data class PlanWrite(val planId: String? = null) : DetailRoute

    @Serializable
    data object ProfileUpdate : DetailRoute
}