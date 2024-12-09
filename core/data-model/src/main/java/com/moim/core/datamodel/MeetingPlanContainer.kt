package com.moim.core.datamodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingPlanContainer(
    @SerialName("plans")
    val plans: List<PlanResponse>,
    @SerialName("meets")
    val meetings: List<MeetingResponse>
)