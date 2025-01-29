package com.moim.core.datamodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlanResponse(
    @SerialName("creatorId")
    val userId: String = "",
    @SerialName("planId")
    val planId: String = "",
    @SerialName("meetId")
    val meetingId: String = "",
    @SerialName("meetName")
    val meetingName: String = "",
    @SerialName("meetImage")
    val meetingImage: String = "",
    @SerialName("planName")
    val planName: String = "",
    @SerialName("planMemberCount")
    val planMemberCount: Int = 1,
    @SerialName("planTime")
    val planTime: String = "",
    @SerialName("planAddress")
    val planAddress: String = "",
    @SerialName("planLongitude")
    val planLongitude: Double = 0.0,
    @SerialName("planLatitude")
    val planLatitude: Double = 0.0,
    @SerialName("weatherIcon")
    val weatherIconUrl: String = "",
    @SerialName("temperature")
    val temperature: Float = 0f,
    @SerialName("participant")
    val isParticipant: Boolean = false,
)