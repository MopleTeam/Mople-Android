package com.moim.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewResponse(
    @SerialName("meetId")
    val meetingId: String,
    @SerialName("reviewId")
    val reviewId: String,
    @SerialName("reviewName")
    val reviewName: String,
    @SerialName("address")
    val address: String,
    @SerialName("lat")
    val latitude: Double = 0.0, // x
    @SerialName("lot")
    val longitude: Double = 0.0, // y
    @SerialName("participantsCount")
    val memberCount: Int = 1,
    @SerialName("images")
    val images: List<String> = emptyList(),
    @SerialName("reviewDateTime")
    val reviewAt: String,
)