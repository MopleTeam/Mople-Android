package com.moim.core.model.item

import android.os.Bundle
import androidx.navigation.NavType
import com.moim.core.model.Plan
import com.moim.core.model.Review
import com.moim.core.model.ReviewImage
import com.moim.core.model.util.encoding
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class PlanItem(
    val isPlanAtBefore: Boolean = true,
    val userId: String = "",
    val postId: String = "",
    val commentCheckId: String = "",
    val meetingId: String = "",
    val meetingName: String = "",
    val meetingImageUrl: String = "",
    val planName: String = "",
    val participantsCount: Int = 1,
    val planAt: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val placeName: String = "",
    val weatherIconUrl: String = "",
    val temperature: Float = 0f,
    val reviewImages: List<ReviewImage> = emptyList(),
)

fun PlanItem.asPlan() : Plan {
    return Plan(
        userId = userId,
        meetingId = meetingId,
        meetingName = meetingName,
        meetingImageUrl = meetingImageUrl,
        planId = postId,
        planName = planName,
        planMemberCount = participantsCount,
        planTime = planAt,
        planAddress = address,
        planLongitude = longitude,
        planLatitude = latitude,
        placeName = placeName,
        weatherIconUrl = weatherIconUrl,
        temperature = temperature,
    )
}

fun Plan.asPlanItem(): PlanItem {
    return PlanItem(
        isPlanAtBefore = true,
        userId = userId,
        postId = planId,
        commentCheckId = planId,
        meetingId = meetingId,
        meetingName = meetingName,
        meetingImageUrl = meetingImageUrl,
        planName = planName,
        participantsCount = planMemberCount,
        planAt = planTime,
        address = planAddress,
        latitude = planLatitude,
        longitude = planLongitude,
        placeName = placeName,
        temperature = temperature,
        weatherIconUrl = weatherIconUrl,
        reviewImages = emptyList()
    )
}

fun Review.asPlanItem(): PlanItem {
    return PlanItem(
        isPlanAtBefore = false,
        userId = userId,
        postId = reviewId,
        commentCheckId = postId,
        meetingId = meetingId,
        meetingName = meetingName,
        meetingImageUrl = meetingImageUrl,
        planName = reviewName,
        participantsCount = memberCount,
        planAt = reviewAt,
        address = address,
        latitude = latitude,
        longitude = longitude,
        placeName = placeName,
        reviewImages = images
    )
}

val PlanItemType = object : NavType<PlanItem?>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): PlanItem? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): PlanItem? {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: PlanItem?) {
        if (value != null) bundle.putString(key, Json.encodeToString(PlanItem.serializer(), value))
    }

    override fun serializeAsValue(value: PlanItem?): String {
        return value
            ?.let {
                Json.encodeToString(
                    serializer = PlanItem.serializer(),
                    value = it.copy(meetingImageUrl = it.meetingImageUrl.encoding())
                )
            }
            ?: ""
    }
}