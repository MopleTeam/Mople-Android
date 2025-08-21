package com.moim.core.datamodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HolidayResponse(
    @SerialName("title")
    val title: String,
    @SerialName("date")
    val date: String
)