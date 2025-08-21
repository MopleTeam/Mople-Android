package com.moim.core.model

import androidx.compose.runtime.Stable
import com.moim.core.model.util.KZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Stable
@Serializable
data class Plan(
    val userId: String = "",
    val meetingId: String = "",
    val meetingName: String = "",
    val meetingImageUrl: String = "",
    val planId: String = "",
    val planName: String = "",
    val planMemberCount: Int = 0,
    val planAddress: String = "",
    val planLongitude: Double = 0.0,
    val planLatitude: Double = 0.0,
    val placeName: String = "",
    val weatherAddress: String = "",
    val weatherIconUrl: String = "",
    val temperature: Float = 0f,
    val isParticipant: Boolean = true,
    val commentCount : Int = 0,
    @Serializable(KZonedDateTimeSerializer::class)
    val planAt: ZonedDateTime = ZonedDateTime.now(),
)
