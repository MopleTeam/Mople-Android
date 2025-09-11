package com.moim.core.common.model

import android.os.Bundle
import androidx.compose.runtime.Stable
import androidx.navigation.NavType
import com.moim.core.common.model.util.KZonedDateTimeSerializer
import com.moim.core.common.model.util.encoding
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime

@Stable
@Serializable
data class Meeting(
    val id: String = "",
    val creatorId: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val memberCount: Int = 1,
    val sinceDays: Int = 0,
    @Serializable(with = KZonedDateTimeSerializer::class)
    val lastPlanAt: ZonedDateTime? = null,
) {
    var isDeleted : Boolean = false
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
        return value
            ?.let {
                Json.encodeToString(
                    serializer = Meeting.serializer(),
                    value = it.copy(imageUrl = it.imageUrl.encoding())
                )
            }
            ?: ""
    }
}