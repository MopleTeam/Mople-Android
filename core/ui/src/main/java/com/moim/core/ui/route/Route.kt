package com.moim.core.ui.route

import androidx.annotation.DrawableRes
import androidx.navigation3.runtime.NavKey
import com.moim.core.common.model.Comment
import com.moim.core.common.model.Meeting
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.model.item.PlanItem
import kotlinx.serialization.Serializable

sealed interface Route

sealed interface IntroRoute : Route, NavKey {

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

@Serializable
sealed interface MainRoute : Route, NavKey {

    @Serializable
    data object Home : MainRoute

    @Serializable
    data object Meeting : MainRoute

    @Serializable
    data object Calendar : MainRoute

    @Serializable
    data object Profile : MainRoute
}

@Serializable
sealed interface DetailRoute : Route, NavKey {

    @Serializable
    data class MeetingDetail(
        val meetingId: String
    ) : DetailRoute

    @Serializable
    data class MeetingWrite(
        val meeting: Meeting? = null
    ) : DetailRoute

    @Serializable
    data class MeetingSetting(
        val meeting: Meeting
    ) : DetailRoute

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
    ) : DetailRoute
    @Serializable
    data class CommentDetail(
        val meetId: String,
        val postId: String,
        val comment: Comment? = null,
    ) : DetailRoute

    @Serializable
    data class PlanWrite(
        val planItem: PlanItem? = null
    ) : DetailRoute
    @Serializable
    data class ReviewWrite(
        val postId: String,
        val isUpdated: Boolean
    ) : DetailRoute

    @Serializable
    data class ParticipantList(
        val viewIdType: ViewIdType,
    ) : DetailRoute

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