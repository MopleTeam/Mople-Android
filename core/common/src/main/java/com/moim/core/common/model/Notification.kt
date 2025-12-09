package com.moim.core.common.model

import java.time.ZonedDateTime

enum class NotificationType {
    MEET_NEW_MEMBER,
    PLAN_CREATE,
    PLAN_UPDATE,
    PLAN_DELETE,
    PLAN_REMIND,
    REVIEW_REMIND,
    REVIEW_UPDATE,
    COMMENT_MENTION,
    COMMENT_REPLY,
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
    val message: String,
    val planDate: ZonedDateTime?,
    val sendAt: ZonedDateTime,
)
