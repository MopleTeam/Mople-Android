package com.moim.core.remote.model

import com.moim.core.common.model.MeetingPlanContainer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingPlanContainerResponse(
    @SerialName("plans")
    val plans: List<PlanResponse>,
    @SerialName("meets")
    val meetings: List<MeetingResponse>
)

fun MeetingPlanContainerResponse.asItem() : MeetingPlanContainer {
    return MeetingPlanContainer(
        plans = plans.map(PlanResponse::asItem).map { it.copy(isParticipant = true) },
        meetings = meetings.map(MeetingResponse::asItem)
    )
}