package com.moim.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingPlanResponse(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("meetingId")
    val meetingId: String,
    @SerialName("meetingName")
    val meetingName: String,
    @SerialName("participants")
    val participants: List<ParticipantResponse>,
    @SerialName("address")
    val address: String,
    @SerialName("detailAddress")
    val detailAddress: String,
    @SerialName("longitude")
    val longitude: Long,
    @SerialName("latitude")
    val latitude: Long,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("startAt")
    val startedAt: String,
    @SerialName("temperature")
    val temperature: Float,
    @SerialName("weatherIconUrl")
    val weatherIconUrl: String
)

@Serializable
data class ParticipantResponse(
    @SerialName("id")
    val id: String,
    @SerialName("userId")
    val userId: String,
    @SerialName("userNickname")
    val userNickname: String,
    @SerialName("userProfileImgUrl")
    val userProfileUrl: String,
)