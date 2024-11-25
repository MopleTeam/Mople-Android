package com.moim.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingResponse(
    @SerialName("meetId")
    val id: String,
    @SerialName("meetName")
    val name: String,
    @SerialName("meetImage")
    val imageUrl: String = "",
    @SerialName("members")
    val memberCount: Int = 1,
    @SerialName("lastPlanDays")
    val lastPlanAt: String? = null
)

@Serializable
data class MemberResponse(
    @SerialName("nickname")
    val userNickname: String,
    @SerialName("profileImg")
    val imageUrl: String? = null,
)