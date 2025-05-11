package com.moim.core.model

import com.moim.core.datamodel.NotificationResponse
import com.moim.core.datamodel.PayloadResponse

enum class NotificationType {
    MEET_NEW_MEMBER,
    PLAN_CREATE,
    PLAN_UPDATE,
    PLAN_DELETE,
    PLAN_REMIND,
    REVIEW_REMIND,
    REVIEW_UPDATE,
    NONE
}

data class Notification(
    val notificationId: String,
    val meetName: String,
    val meetImgUrl: String,
    val meetId: String?,
    val planId: String?,
    val reviewId: String?,
    val type: NotificationType,
    val payload: Payload,
    val sendAt: String,
)

data class Payload(
    val title: String,
    val message: String
)

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
        sendAt = sendAt,
    )
}

fun PayloadResponse.asItem(): Payload {
    return Payload(
        title = title,
        message = message
    )
}