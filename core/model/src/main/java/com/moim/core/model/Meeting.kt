package com.moim.core.model

import android.os.Bundle
import androidx.compose.runtime.Stable
import androidx.navigation.NavType
import com.moim.core.data.model.MeetingResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Stable
@Serializable
data class Meeting(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val memberCount: Int = 1,
    val lastPlanAt: String? = null
)

fun MeetingResponse.asItem(): Meeting {
    return Meeting(
        id = id,
        name = name,
        imageUrl = imageUrl,
        memberCount = memberCount,
        lastPlanAt = lastPlanAt
    )
}

val MeetingType = object : NavType<Meeting?>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): Meeting? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): Meeting? {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: Meeting?) {
        if (value != null) bundle.putString(key, Json.encodeToString(Meeting.serializer(), value))
    }

    override fun serializeAsValue(value: Meeting?): String {
        return if (value == null) "" else Json.encodeToString(Meeting.serializer(), value)
    }
}