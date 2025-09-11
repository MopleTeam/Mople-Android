package com.moim.core.remote.model

import com.moim.core.common.model.Place
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaceResponseContainer(
    @SerialName("searchResult")
    val locations: List<PlaceResponse>,
    @SerialName("page")
    val page: Int = 1,
    @SerialName("isEnd")
    val isEnd: Boolean = true
)

@Serializable
data class PlaceResponse(
    @SerialName("distance")
    val distance: String,
    @SerialName("title")
    val title: String,
    @SerialName("address")
    val address: String = "",
    @SerialName("roadAddress")
    val roadAddress: String,
    @SerialName("x")
    val xPoint: String,
    @SerialName("y")
    val yPoint: String
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