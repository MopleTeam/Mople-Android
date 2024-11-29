package com.moim.core.model

import android.os.Bundle
import androidx.compose.runtime.Stable
import androidx.navigation.NavType
import com.moim.core.data.model.PlanResponse
import com.moim.core.model.util.encoding
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Stable
@Serializable
data class Plan(
    val meetingId: String = "",
    val meetingName: String = "",
    val meetingImage: String = "",
    val planId: String = "",
    val planName: String = "",
    val planMemberCount: Int = 0,
    val planTime: String = "",
    val planAddress: String = "",
    val weatherIconUrl: String = "",
    val temperature: Float = 0f,
    val isParticipant: Boolean = false,
    val planLongitude: Double = 0.0,
    val planLatitude: Double = 0.0,
)

fun PlanResponse.asItem(): Plan {
    return Plan(
        meetingId = meetingId,
        meetingName = meetingName,
        meetingImage = meetingImage,
        planId = planId,
        planName = planName,
        planMemberCount = planMemberCount,
        planTime = planTime,
        planLatitude = planLatitude,
        planLongitude = planLongitude,
        planAddress = planAddress,
        weatherIconUrl = weatherIconUrl,
        temperature = temperature,
        isParticipant = isParticipant
    )
}

val PlanType = object : NavType<Plan?>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): Plan? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): Plan? {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: Plan?) {
        if (value != null) bundle.putString(key, Json.encodeToString(Plan.serializer(), value))
    }

    override fun serializeAsValue(value: Plan?): String {
        return value
            ?.let {
                Json.encodeToString(
                    serializer = Plan.serializer(),
                    value = it.copy(
                        meetingImage = it.meetingImage.encoding(),
                        weatherIconUrl = it.weatherIconUrl.encoding()
                    )
                )
            }
            ?: ""
    }
}