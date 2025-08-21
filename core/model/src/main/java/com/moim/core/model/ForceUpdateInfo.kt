package com.moim.core.model

data class ForceUpdateInfo(
    val isForceUpdate: Boolean,
    val minVersion: String,
    val message: String = "",
)
