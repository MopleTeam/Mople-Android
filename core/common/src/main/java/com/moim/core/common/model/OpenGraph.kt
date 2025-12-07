package com.moim.core.common.model

import kotlinx.serialization.Serializable

@Serializable
data class OpenGraph(
    val url: String,
    val title: String?,
    val description: String?,
    val imageUrl: String?,
) {
    val isEmpty
        get() = title.isNullOrEmpty() && description.isNullOrEmpty() && imageUrl.isNullOrEmpty()
}