package com.moim.feature.mapdetail.model

import com.moim.core.common.model.MapType

sealed interface MapDetailSideEffect {
    data object NavigateToBack : MapDetailSideEffect

    data class NavigateToMapApp(
        val mapType: MapType,
        val latitude: Double,
        val longitude: Double,
        val address: String,
    ) : MapDetailSideEffect
}
