package com.moim.core.model

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class Review(
    val userId: String = "",
    val meetingId: String,
    val meetingName: String = "",
    val meetingImageUrl: String = "",
    val postId: String,
    val reviewId: String,
    val reviewName: String,
    val address: String,
    val latitude: Double = 0.0, // x
    val longitude: Double = 0.0, // y
    val placeName: String = "",
    val memberCount: Int = 1,
    val images: List<ReviewImage> = emptyList(),
    val reviewAt: String,
)

@Stable
@Serializable
data class ReviewImage(
    val imageId: String,
    val imageUrl: String,
)
