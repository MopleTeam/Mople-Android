package com.moim.core.model.item

import android.os.Bundle
import androidx.navigation.NavType
import com.moim.core.model.Plan
import com.moim.core.model.Review
import com.moim.core.model.util.encoding
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class PlanItem(
    val isPlanAtBefore: Boolean = true,
    val userId: String = "",
    val postId: String = "",
    val meetingId: String = "",
    val meetingName: String = "",
    val meetingImageUrl: String = "",
    val planName: String = "",
    val participantsCount: Int = 1,
    val planAt: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val images: List<String> = emptyList(),
)

fun Plan.asPlanItem(): PlanItem {
    return PlanItem(
        isPlanAtBefore = true,
        userId = this.userId,
        postId = this.planId,
        meetingId = this.meetingId,
        meetingName = this.meetingName,
        meetingImageUrl = this.meetingImageUrl,
        planName = this.planName,
        participantsCount = this.planMemberCount,
        planAt = this.planTime,
        address = this.planAddress,
        latitude = this.planLatitude,
        longitude = this.planLongitude,
        images = emptyList()
    )
}

fun Review.asPlanItem(): PlanItem {
    return PlanItem(
        isPlanAtBefore = false,
        userId = this.userId,
        postId = this.postId,
        meetingId = this.meetingId,
        meetingName = this.meetingName,
        meetingImageUrl = this.meetingImageUrl,
        planName = this.reviewName,
        participantsCount = this.memberCount,
        planAt = this.reviewAt,
        address = this.address,
        latitude = this.latitude,
        longitude = this.longitude,
        images = this.images
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
                    value = it.copy(
                        meetingImageUrl = it.meetingImageUrl.encoding(),
                    )
                )
            }
            ?: ""
    }
}