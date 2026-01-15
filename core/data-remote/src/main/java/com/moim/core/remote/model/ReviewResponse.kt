package com.moim.core.remote.model

import com.moim.core.common.model.Review
import com.moim.core.common.model.ReviewImage
import com.moim.core.common.util.parseZonedDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewResponse(
    @SerialName("creatorId")
    val userId: String = "",
    @SerialName("meetId")
    val meetingId: String = "",
    @SerialName("meetName")
    val meetingName: String = "",
    @SerialName("meetImg")
    val meetingImageUrl: String = "",
    @SerialName("reviewId")
    val reviewId: String,
    @SerialName("postId")
    val postId: String = "",
    @SerialName("reviewName")
    val reviewName: String,
    @SerialName("address")
    val address: String = "",
    @SerialName("lat")
    val latitude: Double = 0.0, // x
    @SerialName("lot")
    val longitude: Double = 0.0, // y
    @SerialName("title")
    val placeName: String = "",
    @SerialName("description")
    val description: String = "",
    @SerialName("participantsCount")
    val memberCount: Int = 1,
    @SerialName("commentCount")
    val commentCount: Int = 0,
    @SerialName("temperature")
    val temperature: Float = 0f,
    @SerialName("weatherIcon")
    val weatherIcon: String = "",
    @SerialName("weatherAddress")
    val weatherAddress: String = "",
    @SerialName("images")
    val images: List<ReviewImageResponse> = emptyList(),
    @SerialName("reviewTime")
    val reviewAt: String = "",
)

@Serializable
data class ReviewImageResponse(
    @SerialName("imageId")
    val imageId: String,
    @SerialName("reviewImg")
    val reviewImageUrl: String,
)

fun ReviewResponse.asItem(): Review =
    Review(
        userId = userId,
        meetingId = meetingId,
        meetingName = meetingName,
        meetingImageUrl = meetingImageUrl,
        postId = postId,
        reviewId = reviewId,
        reviewName = reviewName,
        description = description,
        address = address,
        latitude = latitude,
        longitude = longitude,
        placeName = placeName,
        memberCount = memberCount,
        commentCount = commentCount,
        images = images.map(ReviewImageResponse::asItem),
        temperature = temperature,
        weatherAddress = weatherAddress,
        weatherIcon = weatherIcon,
        reviewAt = reviewAt.parseZonedDateTime(),
    )

fun ReviewImageResponse.asItem(): ReviewImage =
    ReviewImage(
        imageId = imageId,
        imageUrl = reviewImageUrl,
    )
