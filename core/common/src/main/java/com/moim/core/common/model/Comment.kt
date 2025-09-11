package com.moim.core.common.model

import androidx.compose.runtime.Stable
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import com.moim.core.common.model.util.KZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime

@Stable
@Serializable
data class Comment(
    val postId: String = "",
    val commentId: String = "",
    val content: String = "",
    val parentId: String? = null,
    val replayCount: Int = 0,
    val likeCount: Int = 0,
    val isLike: Boolean = false,
    val writer: Writer,
    val mentions: List<Writer> = emptyList(),
    @Serializable(with = KZonedDateTimeSerializer::class)
    val commentAt: ZonedDateTime,
)

fun Comment.isChild(): Boolean {
    return parentId != null
}

@Stable
@Serializable
data class Writer(
    val userId: String,
    val nickname: String,
    val imageUrl: String,
)

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