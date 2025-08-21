package com.moim.core.data.mapper

import com.moim.core.datamodel.PlaceResponse
import com.moim.core.model.Place

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