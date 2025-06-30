package com.moim.core.model

import androidx.compose.runtime.Stable
import com.moim.core.datamodel.HolidayResponse

@Stable
data class Holiday(
    val title: String,
    val date: String
)

fun HolidayResponse.asItem() : Holiday {
    return Holiday(
        title = title,
        date = date
    )
}