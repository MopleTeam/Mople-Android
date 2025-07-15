package com.moim.core.data.mapper

import com.moim.core.datamodel.MeetingResponse
import com.moim.core.model.Meeting

fun MeetingResponse.asItem(): Meeting {
    return Meeting(
        id = id,
        creatorId = creatorId,
        name = name,
        imageUrl = imageUrl,
        memberCount = memberCount,
        lastPlanAt = lastPlanAt,
        sinceDays = sinceDays
    )
}