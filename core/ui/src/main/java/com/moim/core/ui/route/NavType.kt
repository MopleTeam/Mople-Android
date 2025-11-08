package com.moim.core.ui.route

import android.os.Bundle
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import com.moim.core.common.model.Comment
import com.moim.core.common.model.Meeting
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.model.item.PlanItem
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

val MeetingType = object : NavType<Meeting?>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): Meeting? {
        return bundle.getString(key)?.let {
            Json.decodeFromString(it) }
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

val CommentType = object : NavType<Comment?>(isNullableAllowed = true) {
    override fun get(bundle: SavedState, key: String): Comment? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): Comment? {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: SavedState, key: String, value: Comment?) {
        if (value != null) bundle.putString(key, Json.encodeToString(Comment.serializer(), value))
    }

    override fun serializeAsValue(value: Comment?): String {
        return value
            ?.let {
                Json.encodeToString(
                    serializer = Comment.serializer(),
                    value = it,
                )
            }
            ?: ""
    }
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

fun String.encoding(encoding: String = StandardCharsets.UTF_8.toString()): String {
    return URLEncoder.encode(this, encoding)
}