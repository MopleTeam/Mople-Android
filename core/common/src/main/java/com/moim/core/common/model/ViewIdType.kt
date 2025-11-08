package com.moim.core.common.model

import androidx.compose.runtime.Stable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Stable
@Serializable
sealed class ViewIdType {
    abstract val id: String

    @Serializable
    @SerialName("PostId")
    data class PostId(override val id: String) : ViewIdType()

    @Serializable
    @SerialName("PlanId")
    data class PlanId(override val id: String) : ViewIdType()

    @Serializable
    @SerialName("ReviewId")
    data class ReviewId(override val id: String) : ViewIdType()

    @Serializable
    @SerialName("MeetId")
    data class MeetId(override val id: String) : ViewIdType()
}
