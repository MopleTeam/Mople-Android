package com.moim.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingResponse(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("imageUrl")
    val imageUrl: String = "",
    @SerialName("creatorId")
    val creatorId: String,
    @SerialName("creatorNickname")
    val creatorNickname: String,
    @SerialName("members")
    val members: List<MemberResponse>,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("latestPlanStartAt")
    val lastPlanAt: String = ""
)

@Serializable
data class MemberResponse(
    @SerialName("id")
    val id: String,
    @SerialName("userId")
    val userId: String,
    @SerialName("userNickname")
    val userNickname: String,
    @SerialName("joinedAt")
    val joinedAt: String
)