package com.moim.core.datamodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponse(
    @SerialName("notificationId")
    val notificationId: String,
    @SerialName("meetId")
    val meetId: String? = null,
    @SerialName("planId")
    val planId: String? = null,
    @SerialName("reviewId")
    val reviewId: String? = null,
    @SerialName("type")
    val type: String,
    @SerialName("payload")
    val payload: PayloadResponse
)

@Serializable
data class PayloadResponse(
    @SerialName("title")
    val title: String,
    @SerialName("message")
    val message: String
)