package com.moim.feature.alarm.model

import com.moim.core.common.model.Notification
import com.moim.core.common.model.NotificationType
import java.time.ZonedDateTime

private val boldRegex = Regex("<highlight>(.*?)</highlight>")

data class AlarmUiModel(
    val notificationId: String,
    val meetName: String,
    val meetId: String?,
    val planId: String?,
    val reviewId: String?,
    val title: String,
    val description: String,
    val targetKeyword: String,
    val type: NotificationType,
    val meetImageUrl: String,
    val planDate: ZonedDateTime?,
    val sendAt: ZonedDateTime,
)

fun Notification.asUiModel(): AlarmUiModel {
    val targetKeyword = boldRegex.find(message)?.groupValues?.getOrNull(1)
    val remainingText = message.replace(boldRegex, "").trim()
    return AlarmUiModel(
        notificationId = notificationId,
        meetName = meetName,
        meetImageUrl = meetImgUrl,
        meetId = meetId,
        planId = planId,
        reviewId = reviewId,
        title = remainingText,
        targetKeyword = targetKeyword ?: "",
        description = message,
        type = type,
        planDate = planDate,
        sendAt = sendAt,
    )
}