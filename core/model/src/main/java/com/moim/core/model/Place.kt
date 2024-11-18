package com.moim.core.model

import androidx.compose.runtime.Stable
import com.moim.core.data.model.PlaceResponse

@Stable
data class Place(
    val title: String = "",
    val distance: String = "",
    val roadAddress: String = "",
    val xPoint: String = "",
    val yPoint: String = ""
)

fun PlaceResponse.asItem(): Place {
    return Place(
        title = title,
        distance = distance,
        roadAddress = roadAddress,
        xPoint = xPoint,
        yPoint = yPoint
    )
}