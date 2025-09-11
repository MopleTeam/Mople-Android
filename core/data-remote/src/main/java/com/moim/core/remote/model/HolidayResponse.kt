package com.moim.core.remote.model

import com.moim.core.common.model.Holiday
import com.moim.core.common.util.parseDateStringToZonedDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HolidayResponse(
    @SerialName("title")
    val title: String,
    @SerialName("date")
    val date: String
)

fun HolidayResponse.asItem() : Holiday {
    return Holiday(
        title = title,
        date = date.parseDateStringToZonedDateTime()
    )
}