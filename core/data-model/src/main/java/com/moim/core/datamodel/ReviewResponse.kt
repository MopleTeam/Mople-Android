package com.moim.core.datamodel

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
    @SerialName("participantsCount")
    val memberCount: Int = 1,
    @SerialName("images")
    val images: List<ReviewImageResponse> = emptyList(),
    @SerialName("reviewTime")
    val reviewAt: String = "",
)

@Serializable
data class ReviewImageResponse(
    @SerialName("imageId")
    val imageId :String,
    @SerialName("reviewImg")
    val reviewImageUrl: String
)