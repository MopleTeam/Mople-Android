package com.moim.core.model

import com.moim.core.datamodel.ForceUpdateResponse

data class ForceUpdateInfo(
    val isForceUpdate: Boolean,
    val minVersion: String,
    val message: String = "",
)

fun ForceUpdateResponse.asItem(): ForceUpdateInfo {
    return ForceUpdateInfo(
        isForceUpdate = isForceUpdate,
        minVersion = minVersion,
        message = message
    )
}