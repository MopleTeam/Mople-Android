package com.moim.core.remote.model

import com.moim.core.common.model.ForceUpdateInfo
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

fun ForceUpdateResponse.asItem(): ForceUpdateInfo =
    ForceUpdateInfo(
        isForceUpdate = isForceUpdate,
        minVersion = minVersion,
        message = message,
    )
