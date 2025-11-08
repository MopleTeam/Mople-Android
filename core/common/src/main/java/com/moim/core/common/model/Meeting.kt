package com.moim.core.common.model

import androidx.compose.runtime.Stable
import com.moim.core.common.model.util.KZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Stable
@Serializable
data class Meeting(
    val id: String = "",
    val creatorId: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val memberCount: Int = 1,
    val sinceDays: Int = 0,
    @Serializable(with = KZonedDateTimeSerializer::class)
    val lastPlanAt: ZonedDateTime? = null,
) {
    var isDeleted : Boolean = false
}