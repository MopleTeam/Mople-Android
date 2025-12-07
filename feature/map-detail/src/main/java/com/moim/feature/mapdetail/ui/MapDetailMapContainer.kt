package com.moim.feature.mapdetail.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.mapdetail.MapDetailUiAction
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.MarkerComposable
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberUpdatedMarkerState

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapContainer(
    modifier: Modifier = Modifier,
    latitude: Double,
    longitude: Double,
    onUiAction: (MapDetailUiAction) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState()
    val markerState = rememberUpdatedMarkerState(position = LatLng(latitude, longitude))

    LaunchedEffect(latitude, longitude) {
        if (longitude != 0.0 && latitude != 0.0) {
            cameraPositionState.position = CameraPosition(LatLng(latitude, longitude), 15.2)
        }
    }

    NaverMap(
        modifier = modifier.fillMaxSize(),
        uiSettings = MapUiSettings(
            isZoomControlEnabled = false,
            isScaleBarEnabled = false,
            isLogoClickEnabled = false,
            isCompassEnabled = false
        ),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(locationTrackingMode = LocationTrackingMode.Follow),
        onLocationChange = {}
    ) {
        if (longitude != 0.0 && latitude != 0.0) {
            MarkerComposable(
                keys = arrayOf("location"),
                state = markerState,
                onClick = {
                    onUiAction(MapDetailUiAction.OnShowPlaceInfoDialog(true))
                    true
                }
            ) {
                Icon(
                    modifier = Modifier.size(54.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_location),
                    contentDescription = "",
                    tint = MoimTheme.colors.primary.primary
                )
            }
        }
    }
}