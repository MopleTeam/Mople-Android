package com.moim.core.remote.model

import com.moim.core.common.model.Participant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParticipantContainerResponse(
    @SerialName("creatorId")
    val creatorId: String,
    @SerialName("members")
    val members: List<ParticipantResponse>,
)

@Serializable
data class ParticipantResponse(
    @SerialName("memberId")
    val memberId: String,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("profileImg")
    val profileImg: String = "",
)

fun ParticipantResponse.asItem(isCreator: Boolean): Participant =
    Participant(
        isCreator = isCreator,
        memberId = memberId,
        nickname = nickname,
        imageUrl = profileImg,
    )
