package com.moim.core.model

import androidx.compose.runtime.Stable
import com.moim.core.data.model.ActiveCommentResponse
import com.moim.core.data.model.MeetingResponse
import com.moim.core.data.model.ParticipantResponse

@Stable
data class MeetingInfo(
    val id: String = "",
    val name: String = "",
    val meetingId: String = "",
    val meetingName: String = "",
    val participants: List<Participant> = emptyList(),
    val address: String = "",
    val detailAddress: String = "",
    val longitude: Long = 0,
    val latitude: Long = 0,
    val createdAt: String = "",
    val startedAt: String = "",
    val endedAt: String = "",
    val activeComments: List<ActiveComment> = emptyList(),
    val temperature: Float = 0f,
    val weatherIconUrl: String = ""
)

@Stable
data class Participant(
    val id: String = "",
    val userId: String = "",
    val userNickname: String = "",
    val userProfileUrl: String = "",
)

@Stable
data class ActiveComment(
    val id: String = "",
    val creatorId: String = "",
    val creatorNickname: String = "",
    val creatorProfileUrl: String = "",
    val contents: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)

fun MeetingResponse.asItem(): MeetingInfo {
    return MeetingInfo(
        id = id,
        name = name,
        meetingId = meetingId,
        meetingName = meetingName,
        participants = participants.map(ParticipantResponse::asItem),
        address = address,
        detailAddress = detailAddress,
        longitude = longitude,
        latitude = latitude,
        createdAt = createdAt,
        startedAt = startedAt,
        endedAt = endedAt,
        activeComments = activeComments.map(ActiveCommentResponse::asItem),
        temperature = temperature,
        weatherIconUrl = weatherIconUrl
    )
}

fun ParticipantResponse.asItem(): Participant {
    return Participant(
        id = id,
        userId = userId,
        userNickname = userNickname,
        userProfileUrl = userProfileUrl
    )
}

fun ActiveCommentResponse.asItem(): ActiveComment {
    return ActiveComment(
        id = id,
        creatorId = creatorId,
        creatorNickname = creatorNickname,
        creatorProfileUrl = creatorProfileUrl,
        contents = contents,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}