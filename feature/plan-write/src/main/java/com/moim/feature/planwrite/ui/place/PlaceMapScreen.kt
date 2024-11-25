package com.moim.feature.planwrite.ui.place

import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun PlaceMapScreen(
    modifier: Modifier = Modifier,
    x: Double,
    y: Double,
    onLocationChange: (Location) -> Unit,
) {
    val cameraPositionState = rememberCameraPositionState {
        if (x != 0.0 && y != 0.0) position = CameraPosition(LatLng(x, y), 15.2)
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
        locationSource = rememberFusedLocationSource(),
        properties = MapProperties(locationTrackingMode = LocationTrackingMode.Follow),
        onLocationChange = onLocationChange
    ) {

    }
}