package com.moim.feature.mapdetail

import androidx.lifecycle.SavedStateHandle
import com.moim.core.common.model.MapType
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    private val placeName
        get() = savedStateHandle.get<String>(KEY_PLACE_NAME) ?: ""

    private val address
        get() = savedStateHandle.get<String>(KEY_ADDRESS) ?: ""

    private val longitude
        get() = savedStateHandle.get<Double>(KEY_LONGITUDE) ?: 0.0

    private val latitude
        get() = savedStateHandle.get<Double>(KEY_LATITUDE) ?: 0.0

    init {
        setUiState(
            MapDetailUiState(
                placeName = placeName,
                address = address,
                longitude = longitude,
                latitude = latitude
            )
        )
    }

    fun onUiAction(uiAction: MapDetailUiAction) {
        when (uiAction) {
            is MapDetailUiAction.OnClickBack -> setUiEvent(MapDetailUiEvent.NavigateToBack)
            is MapDetailUiAction.OnClickMapAddress -> navigateToMapApp(uiAction.mapType)
            is MapDetailUiAction.OnShowPlaceInfoDialog -> showPlaceInfoDialog(uiAction.isShow)
            is MapDetailUiAction.OnShowMapAppDialog -> showMapAppDialog(uiAction.isShow)
        }
    }

    private fun navigateToMapApp(mapType: MapType) {
        uiState.checkState<MapDetailUiState> {
            setUiEvent(
                MapDetailUiEvent.NavigateToMapApp(
                    mapType = mapType,
                    latitude = latitude,
                    longitude = longitude,
                    address = address
                )
            )
        }
    }

    private fun showMapAppDialog(isShow: Boolean) {
        uiState.checkState<MapDetailUiState> {
            setUiState(copy(isShowMapAppDialog = isShow))
        }
    }

    private fun showPlaceInfoDialog(isShow: Boolean) {
        uiState.checkState<MapDetailUiState> {
            setUiState(copy(isShowPlaceInfoDialog = isShow))
        }
    }

    companion object {
        private const val KEY_LONGITUDE = "longitude"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_PLACE_NAME = "placeName"
        private const val KEY_ADDRESS = "address"
    }
}

data class MapDetailUiState(
    val placeName: String,
    val address: String,
    val longitude: Double,
    val latitude: Double,
    val isShowPlaceInfoDialog: Boolean = true,
    val isShowMapAppDialog: Boolean = false,
) : UiState

sealed interface MapDetailUiAction : UiAction {
    data object OnClickBack : MapDetailUiAction
    data class OnClickMapAddress(val mapType: MapType) : MapDetailUiAction
    data class OnShowPlaceInfoDialog(val isShow: Boolean) : MapDetailUiAction
    data class OnShowMapAppDialog(val isShow: Boolean) : MapDetailUiAction
}

sealed interface MapDetailUiEvent : UiEvent {
    data object NavigateToBack : MapDetailUiEvent
    data class NavigateToMapApp(
        val mapType: MapType,
        val latitude: Double,
        val longitude: Double,
        val address: String
    ) : MapDetailUiEvent
}