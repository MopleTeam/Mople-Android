package com.moim.core.data.mapper

import com.moim.core.common.util.parseZonedDateTime
import com.moim.core.datamodel.PlanResponse
import com.moim.core.model.Plan

fun PlanResponse.asItem(): Plan {
    return Plan(
        userId = userId,
        meetingId = meetingId,
        meetingName = meetingName,
        meetingImageUrl = meetingImage,
        planId = planId,
        planName = planName,
        planMemberCount = planMemberCount,
        planLatitude = planLatitude,
        planLongitude = planLongitude,
        planAddress = planAddress,
        placeName = placeName,
        weatherAddress = weatherAddress,
        weatherIconUrl = weatherIconUrl,
        temperature = temperature,
        isParticipant = isParticipant,
        commentCount = commentCount,
        planAt = planTime.parseZonedDateTime(),
    )
}
