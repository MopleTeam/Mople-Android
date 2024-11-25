package com.moim.core.model

import com.moim.core.data.model.ReviewResponse
import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val meetingId: String,
    val reviewId: String,
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
        meetingId = meetingId,
        reviewId = reviewId,
        reviewName = reviewName,
        address = address,
        latitude = latitude,
        longitude = longitude,
        memberCount = memberCount,
        images = images,
        reviewAt = reviewAt
    )
}