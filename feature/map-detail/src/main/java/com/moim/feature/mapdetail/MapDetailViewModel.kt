package com.moim.feature.mapdetail

import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.route.DetailRoute
import com.moim.feature.mapdetail.model.MapDetailIntent
import com.moim.feature.mapdetail.model.MapDetailSideEffect
import com.moim.feature.mapdetail.model.MapDetailState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = MapDetailViewModel.Factory::class)
class MapDetailViewModel @AssistedInject constructor(
    @Assisted mapDetailRoute: DetailRoute.MapDetail,
) : MVIViewModel<MapDetailState, MapDetailSideEffect>(mapDetailRoute.asState()) {
    override fun onIntent(intent: Intent) {
        if (intent !is MapDetailIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is MapDetailIntent.BackClick -> {
                    postSideEffect(MapDetailSideEffect.NavigateToBack)
                }

                is MapDetailIntent.MapAddressClick -> {
                    postSideEffect(
                        MapDetailSideEffect.NavigateToMapApp(
                            mapType = intent.mapType,
                            latitude = state.latitude,
                            longitude = state.longitude,
                            address = state.address,
                        ),
                    )
                }

                is MapDetailIntent.PlaceInfoDialogShow -> {
                    reduce { state.copy(isShowPlaceInfoDialog = intent.isShow) }
                }

                is MapDetailIntent.MapAppDialogShow -> {
                    reduce { state.copy(isShowMapAppDialog = intent.isShow) }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(mapDetailRoute: DetailRoute.MapDetail): MapDetailViewModel
    }
}

private fun DetailRoute.MapDetail.asState() =
    MapDetailState(
        placeName = placeName,
        address = address,
        longitude = longitude,
        latitude = latitude,
    )
