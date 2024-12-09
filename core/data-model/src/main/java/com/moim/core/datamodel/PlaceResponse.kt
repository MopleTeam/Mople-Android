package com.moim.core.datamodel

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
    @SerialName("roadAddress")
    val roadAddress: String,
    @SerialName("x")
    val xPoint: String,
    @SerialName("y")
    val yPoint: String
)