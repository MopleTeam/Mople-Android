package com.moim.feature.mapdetail

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.common.consts.MAP_INTENT_FOR_KAKAO
import com.moim.core.common.consts.MAP_INTENT_FOR_NAVER
import com.moim.core.common.model.MapType
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.view.ObserveAsEvents
import com.moim.core.ui.view.showToast
import com.moim.feature.mapdetail.ui.MapContainer
import com.moim.feature.mapdetail.ui.MapDetailMapAppDialog
import com.moim.feature.mapdetail.ui.MapDetailPlaceInfoDialog
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun MapDetailRoute(
    paddingValues: PaddingValues,
    viewModel: MapDetailViewModel = hiltViewModel(),
    navigateToBack: () -> Unit
) {
    val context = LocalContext.current
    val modifier = Modifier.containerScreen(paddingValues, MoimTheme.colors.white)
    val uiState by viewModel.uiState
        .filterIsInstance<MapDetailUiState>()
        .collectAsStateWithLifecycle(null)

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is MapDetailUiEvent.NavigateToBack -> navigateToBack()
            is MapDetailUiEvent.NavigateToMapApp -> {
                val latitude = event.latitude.toString()
                val longitude = event.longitude.toString()

                try {
                    when (event.mapType) {
                        MapType.KAKAO -> {
                            val kakaoMapUrl = MAP_INTENT_FOR_KAKAO.format(latitude, longitude)
                            val intent = Intent(Intent.ACTION_VIEW, kakaoMapUrl.toUri())
                            context.startActivity(intent)
                        }

                        MapType.NAVER -> {
                            val naverMapUrl = MAP_INTENT_FOR_NAVER.format(latitude, longitude, event.address)
                            val intent = Intent.parseUri(naverMapUrl, Intent.URI_INTENT_SCHEME)
                            context.startActivity(intent)
                        }
                    }
                } catch (e: Exception) {
                    showToast(context, context.getString(R.string.map_detail_map_intent_fail))
                }
            }
        }
    }

    uiState?.let {
        MapDetailScreen(
            modifier = modifier,
            uiState = it,
            onUiAction = viewModel::onUiAction
        )
    }
}

@Composable
fun MapDetailScreen(
    modifier: Modifier = Modifier,
    uiState: MapDetailUiState,
    onUiAction: (MapDetailUiAction) -> Unit = {},
) {
    TrackScreenViewEvent(screenName = "map_detail")

    Column(
        modifier = modifier
    ) {
        MoimTopAppbar(
            title = stringResource(R.string.map_detail_title),
            onClickNavigate = { onUiAction(MapDetailUiAction.OnClickBack) }
        )

        Box {
            MapContainer(
                latitude = uiState.latitude,
                longitude = uiState.longitude,
                onUiAction = onUiAction,
            )

            if (uiState.isShowPlaceInfoDialog) {
                MapDetailPlaceInfoDialog(
                    placeName = uiState.placeName,
                    address = uiState.address,
                    onUiAction = onUiAction
                )
            }

            if (uiState.isShowMapAppDialog) {
                MapDetailMapAppDialog(onUiAction = onUiAction)
            }
        }
    }
}