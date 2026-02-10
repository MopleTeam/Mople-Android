package com.moim.core.remote.model

import com.moim.core.common.model.MeetingPlanContainer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingPlanContainerResponse(
    @SerialName("plans")
    val plans: List<PlanResponse> = emptyList(),
    @SerialName("hasJoinedMeet")
    val hasJoinedMeet: Boolean = true,
)

fun MeetingPlanContainerResponse.asItem(): MeetingPlanContainer =
    MeetingPlanContainer(
        plans = plans.map(PlanResponse::asItem).map { it.copy(isParticipant = true) },
        hasJoinedMeet = hasJoinedMeet,
    )
