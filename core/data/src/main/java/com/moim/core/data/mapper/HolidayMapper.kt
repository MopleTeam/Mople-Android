package com.moim.core.data.mapper

import com.moim.core.common.util.parseDateStringToZonedDateTime
import com.moim.core.datamodel.HolidayResponse
import com.moim.core.model.Holiday

fun HolidayResponse.asItem() : Holiday {
    return Holiday(
        title = title,
        date = date.parseDateStringToZonedDateTime()
    )
}