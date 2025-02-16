package com.moim.core.model

import com.moim.core.datamodel.ParticipantResponse
import kotlinx.serialization.Serializable

@Serializable
data class Participant(
    val isCreator: Boolean,
    val memberId: String,
    val nickname: String,
    val imageUrl: String,
)

fun ParticipantResponse.asItem(isCreator: Boolean): Participant {
    return Participant(
        isCreator = isCreator,
        memberId = memberId,
        nickname = nickname,
        imageUrl = profileImg
    )
}