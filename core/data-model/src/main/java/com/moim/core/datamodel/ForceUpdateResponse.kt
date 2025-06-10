package com.moim.core.datamodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ForceUpdateResponse(
    @SerialName("forceUpdate")
    val isForceUpdate: Boolean,
    @SerialName("minVersion")
    val minVersion: String,
    @SerialName("message")
    val message: String = "",
)