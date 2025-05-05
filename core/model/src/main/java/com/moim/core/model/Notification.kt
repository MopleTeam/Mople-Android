package com.moim.core.model

import com.moim.core.datamodel.NotificationResponse
import com.moim.core.datamodel.PayloadResponse

data class Notification(
    val notificationId: String,
    val meetId: String?,
    val planId: String?,
    val reviewId: String?,
    val type: String,
    val payload: Payload
)

data class Payload(
    val title: String,
    val message: String
)

fun NotificationResponse.asItem(): Notification {
    return Notification(
        notificationId = notificationId,
        meetId = meetId,
        planId = planId,
        reviewId = reviewId,
        type = type,
        payload = payload.asItem()
    )
}

fun PayloadResponse.asItem(): Payload {
    return Payload(
        title = title,
        message = message
    )
}