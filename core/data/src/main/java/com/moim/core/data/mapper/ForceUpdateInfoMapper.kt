package com.moim.core.data.mapper

import com.moim.core.datamodel.ForceUpdateResponse
import com.moim.core.model.ForceUpdateInfo

fun ForceUpdateResponse.asItem(): ForceUpdateInfo {
    return ForceUpdateInfo(
        isForceUpdate = isForceUpdate,
        minVersion = minVersion,
        message = message
    )
}