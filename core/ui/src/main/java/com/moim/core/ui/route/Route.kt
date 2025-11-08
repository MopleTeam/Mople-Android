package com.moim.core.ui.route

import androidx.annotation.DrawableRes
import com.moim.core.common.model.Comment
import com.moim.core.common.model.Meeting
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.model.item.PlanItem
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
    data class MeetingDetail(
        val meetingId: String
    ) : DetailRoute

    @Serializable
    data class MeetingWrite(
        val meeting: Meeting? = null
    ) : DetailRoute {
        companion object {
            val typeMap = mapOf(typeOf<Meeting?>() to MeetingType)
        }
    }

    @Serializable
    data class MeetingSetting(
        val meeting: Meeting
    ) : DetailRoute {
        companion object {
            val typeMap = mapOf(typeOf<Meeting>() to MeetingType)
        }
    }

    @Serializable
    data class MapDetail(
        val placeName: String,
        val address: String,
        val latitude: Double,
        val longitude: Double,
    ) : DetailRoute

    @Serializable
    data class PlanDetail(
        val viewIdType: ViewIdType,
    ) : DetailRoute {
        companion object {
            val typeMap = mapOf(typeOf<ViewIdType>() to ViewIdNavType)
        }
    }

    @Serializable
    data class CommentDetail(
        val meetId: String,
        val postId: String,
        val comment: Comment? = null,
    ) : DetailRoute {
        companion object {
            val typeMap = mapOf(typeOf<Comment?>() to CommentType)
        }
    }

    @Serializable
    data class PlanWrite(val plan: PlanItem? = null) : DetailRoute {
        companion object {
            val typeMap = mapOf(typeOf<PlanItem?>() to PlanItemType)
        }
    }

    @Serializable
    data class ReviewWrite(
        val postId: String,
        val isUpdated: Boolean
    ) : DetailRoute

    @Serializable
    data class ParticipantList(
        val viewIdType: ViewIdType,
    ) : DetailRoute {
        companion object {
            val typeMap = mapOf(typeOf<ViewIdType>() to ViewIdNavType)
        }
    }

    @Serializable
    data class ImageViewer(
        val title: String,
        val images: List<String>,
        val position: Int,
        @DrawableRes val defaultImage: Int? = null,
    ) : DetailRoute

    @Serializable
    data object ProfileUpdate : DetailRoute

    @Serializable
    data object Alarm : DetailRoute

    @Serializable
    data object AlarmSetting : DetailRoute

    @Serializable
    data class WebView(
        val webUrl: String
    ) : DetailRoute
}