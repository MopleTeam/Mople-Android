package com.moim.core.data.mapper

import com.moim.core.datamodel.ParticipantResponse
import com.moim.core.model.Participant


fun ParticipantResponse.asItem(isCreator: Boolean): Participant {
    return Participant(
        isCreator = isCreator,
        memberId = memberId,
        nickname = nickname,
        imageUrl = profileImg
    )
}