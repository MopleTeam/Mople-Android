package com.moim.feature.mapdetail.model

import com.moim.core.common.model.MapType
import com.moim.core.ui.mvi.Intent

sealed interface MapDetailIntent : Intent {
    data object BackClick : MapDetailIntent

    data class MapAddressClick(
        val mapType: MapType,
    ) : MapDetailIntent

    data class PlaceInfoDialogShow(
        val isShow: Boolean,
    ) : MapDetailIntent

    data class MapAppDialogShow(
        val isShow: Boolean,
    ) : MapDetailIntent
}
