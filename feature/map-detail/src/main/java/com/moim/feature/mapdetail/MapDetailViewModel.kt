package com.moim.feature.mapdetail

import com.moim.core.common.model.MapType
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = MapDetailViewModel.Factory::class)
class MapDetailViewModel @AssistedInject constructor(
    @Assisted val mapDetail: DetailRoute.MapDetail
) : BaseViewModel() {

    init {
        setUiState(
            MapDetailUiState(
                placeName = mapDetail.placeName,
                address = mapDetail.address,
                longitude = mapDetail.longitude,
                latitude = mapDetail.latitude
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

    @AssistedFactory
    interface Factory {
        fun create(
            mapDetail: DetailRoute.MapDetail,
        ): MapDetailViewModel
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

    data class OnClickMapAddress(
        val mapType: MapType
    ) : MapDetailUiAction

    data class OnShowPlaceInfoDialog(
        val isShow: Boolean
    ) : MapDetailUiAction

    data class OnShowMapAppDialog(
        val isShow: Boolean
    ) : MapDetailUiAction
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