package com.moim.core.model

import android.os.Bundle
import androidx.navigation.NavType
import com.moim.core.datamodel.ReviewResponse
import com.moim.core.model.util.encoding
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Review(
    val userId: String = "",
    val meetingId: String,
    val meetingName: String = "",
    val meetingImageUrl: String = "",
    val reviewId: String,
    val postId: String,
    val reviewName: String,
    val address: String,
    val latitude: Double = 0.0, // x
    val longitude: Double = 0.0, // y
    val memberCount: Int = 1,
    val images: List<String> = emptyList(),
    val reviewAt: String,
)

fun ReviewResponse.asItem(): Review {
    return Review(
        userId = userId,
        meetingId = meetingId,
        meetingName = meetingName,
        meetingImageUrl = meetingImageUrl,
        reviewId = reviewId,
        postId = postId,
        reviewName = reviewName,
        address = address,
        latitude = latitude,
        longitude = longitude,
        memberCount = memberCount,
        images = images,
        reviewAt = reviewAt
    )
}

val ReviewType = object : NavType<Review?>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): Review? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): Review? {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: Review?) {
        if (value != null) bundle.putString(key, Json.encodeToString(Review.serializer(), value))
    }

    override fun serializeAsValue(value: Review?): String {
        return value
            ?.let {
                Json.encodeToString(
                    serializer = Review.serializer(),
                    value = it.copy(
                        meetingImageUrl = it.meetingImageUrl.encoding(),
                        images = it.images.map(String::encoding)
                    )
                )
            }
            ?: ""
    }
}