package com.moim.core.model

import com.moim.core.datamodel.MeetingPlanContainerResponse
import com.moim.core.datamodel.MeetingResponse
import com.moim.core.datamodel.PlanResponse

data class MeetingPlanContainer(
    val plans: List<Plan>,
    val meetings: List<Meeting>
)

fun MeetingPlanContainerResponse.asItem() : MeetingPlanContainer {
    return MeetingPlanContainer(
        plans = plans.map(PlanResponse::asItem).map { it.copy(isParticipant = true) },
        meetings = meetings.map(MeetingResponse::asItem)
    )
}