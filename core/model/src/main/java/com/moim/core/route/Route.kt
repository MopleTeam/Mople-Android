package com.moim.core.route

import com.moim.core.model.Meeting
import com.moim.core.model.MeetingType
import com.moim.core.model.Plan
import com.moim.core.model.PlanType
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

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
    data class MeetingWrite(val meeting: Meeting? = null) : DetailRoute {
        companion object {
            val typeMap = mapOf(typeOf<Meeting?>() to MeetingType)
        }
    }

    @Serializable
    data class MeetingDetail(val meetingId: String) : DetailRoute

    @Serializable
    data class PlanWrite(val plan: Plan? = null) : DetailRoute {
        companion object {
            val typeMap = mapOf(typeOf<Plan?>() to PlanType)
        }
    }

    @Serializable
    data object ProfileUpdate : DetailRoute
}