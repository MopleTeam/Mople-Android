package com.moim.core.remote.model

import com.moim.core.common.model.Meeting
import com.moim.core.common.util.parseZonedDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingResponse(
    @SerialName("meetId")
    val id: String,
    @SerialName("creatorId")
    val creatorId: String = "",
    @SerialName("meetName")
    val name: String,
    @SerialName("meetImage")
    val imageUrl: String = "",
    @SerialName("memberCount")
    val memberCount: Int = 1,
    @SerialName("meetStartDate")
    val meetStartDate: Int = 0,
    @SerialName("lastPlanDay")
    val lastPlanAt: String? = null,
    @SerialName("sinceDays")
    val sinceDays: Int = 0,
)

fun MeetingResponse.asItem(): Meeting {
    return Meeting(
        id = id,
        creatorId = creatorId,
        name = name,
        imageUrl = imageUrl,
        memberCount = memberCount,
        lastPlanAt = lastPlanAt?.parseZonedDateTime(),
        sinceDays = sinceDays
    )
}