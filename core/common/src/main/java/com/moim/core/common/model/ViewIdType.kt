package com.moim.core.common.model

import androidx.compose.runtime.Stable
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Stable
@Serializable
sealed class ViewIdType {
    abstract val id: String

    @Serializable
    @SerialName("PostId")
    data class PostId(override val id: String) : ViewIdType()

    @Serializable
    @SerialName("PlanId")
    data class PlanId(override val id: String) : ViewIdType()

    @Serializable
    @SerialName("ReviewId")
    data class ReviewId(override val id: String) : ViewIdType()

    @Serializable
    @SerialName("MeetId")
    data class MeetId(override val id: String) : ViewIdType()
}

val ViewIdNavType = object : NavType<ViewIdType?>(isNullableAllowed = true) {
    override fun get(bundle: SavedState, key: String): ViewIdType? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): ViewIdType? {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: SavedState, key: String, value: ViewIdType?) {
        if (value != null) bundle.putString(key, Json.encodeToString(ViewIdType.serializer(), value))
    }

    override fun serializeAsValue(value: ViewIdType?): String {
        return value?.let {
            Json.encodeToString(
                serializer = ViewIdType.serializer(),
                value = it,
            )
        } ?: ""
    }
}
