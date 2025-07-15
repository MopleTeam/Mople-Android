package com.moim.core.data.mapper

import com.moim.core.datamodel.MeetingPlanContainerResponse
import com.moim.core.datamodel.MeetingResponse
import com.moim.core.datamodel.PlanResponse
import com.moim.core.model.MeetingPlanContainer

fun MeetingPlanContainerResponse.asItem() : MeetingPlanContainer {
    return MeetingPlanContainer(
        plans = plans.map(PlanResponse::asItem).map { it.copy(isParticipant = true) },
        meetings = meetings.map(MeetingResponse::asItem)
    )
}