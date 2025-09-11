package com.moim.core.remote.model

import com.moim.core.common.model.Notification
import com.moim.core.common.model.NotificationType
import com.moim.core.common.model.Payload
import com.moim.core.common.util.parseZonedDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponse(
    @SerialName("notificationId")
    val notificationId: String,
    @SerialName("meetName")
    val meetName: String = "",
    @SerialName("meetImg")
    val meetImgUrl: String = "",
    @SerialName("meetId")
    val meetId: String? = null,
    @SerialName("planId")
    val planId: String? = null,
    @SerialName("reviewId")
    val reviewId: String? = null,
    @SerialName("type")
    val type: String,
    @SerialName("payload")
    val payload: PayloadResponse,
    @SerialName("planDate")
    val planDate: String?,
    @SerialName("sendAt")
    val sendAt: String,
)

@Serializable
data class PayloadResponse(
    @SerialName("title")
    val title: String,
    @SerialName("message")
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