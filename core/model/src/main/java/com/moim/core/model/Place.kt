package com.moim.core.model

import androidx.compose.runtime.Stable
import com.moim.core.datamodel.PlaceResponse

@Stable
data class Place(
    val title: String = "",
    val address: String = "",
    val distance: String = "",
    val roadAddress: String = "",
    val xPoint: String = "",
    val yPoint: String = ""
)

fun PlaceResponse.asItem(): Place {
    return Place(
        title = title,
        distance = distance,
        address = address,
        roadAddress = roadAddress,
        xPoint = xPoint,
        yPoint = yPoint
    )
}