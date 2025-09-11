package com.moim.core.data.mapper

import com.moim.core.common.util.parseZonedDateTime
import com.moim.core.datamodel.NotificationResponse
import com.moim.core.datamodel.PayloadResponse
import com.moim.core.model.Notification
import com.moim.core.model.NotificationType
import com.moim.core.model.Payload

fun NotificationResponse.asItem(): Notification {
    return Notification(
        notificationId = notificationId,
        meetName = meetName,
        meetImgUrl = meetImgUrl,
        meetId = meetId,
        planId = planId,
        reviewId = reviewId,
        type = NotificationType.entries.find { it.name == type } ?: NotificationType.NONE,
        payload = payload.asItem(),
        planDate = planDate.parseZonedDateTime(),
        sendAt = sendAt.parseZonedDateTime(),
    )
}

fun PayloadResponse.asItem(): Payload {
    return Payload(
        title = title,
        message = message
    )
}