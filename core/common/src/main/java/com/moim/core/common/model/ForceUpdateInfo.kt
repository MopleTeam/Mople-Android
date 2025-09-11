package com.moim.core.common.model

data class ForceUpdateInfo(
    val isForceUpdate: Boolean,
    val minVersion: String,
    val message: String = "",
)
