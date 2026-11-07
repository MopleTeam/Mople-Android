package com.moim.feature.mapdetail.model

import androidx.compose.runtime.Immutable

@Immutable
data class MapDetailState(
    val placeName: String = "",
    val address: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val isShowPlaceInfoDialog: Boolean = true,
    val isShowMapAppDialog: Boolean = false,
)
