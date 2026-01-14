package com.moim.feature.planwrite.ui.place

import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.designsystem.R
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.planwrite.PlanWriteUiAction
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.MarkerComposable
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource
import com.naver.maps.map.compose.rememberUpdatedMarkerState

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun PlaceMapScreen(
    modifier: Modifier = Modifier,
    longitude: Double?,
    latitude: Double?,
    markerLongitude: Double?,
    markerLatitude: Double?,
    onLocationChange: (Location) -> Unit,
    onUiAction: (PlanWriteUiAction) -> Unit,
) {
    val cameraPositionState = rememberCameraPositionState()
    val markerState = rememberUpdatedMarkerState(position = LatLng(markerLatitude ?: 0.0, markerLongitude ?: 0.0))

    TrackScreenViewEvent(screenName = "plan_write_map")

    LaunchedEffect(latitude, longitude) {
        if (longitude != null && latitude != null) {
            cameraPositionState.position = CameraPosition(LatLng(latitude, longitude), 15.2)
        }
    }

    LaunchedEffect(markerLatitude, markerLongitude) {
        if (markerLongitude != null && markerLatitude != null) {
            markerState.position = LatLng(markerLatitude, markerLongitude)
            cameraPositionState.position = CameraPosition(LatLng(markerLatitude, markerLongitude), 15.2)
        }
    }

    NaverMap(
        modifier = modifier.fillMaxSize(),
        uiSettings =
            MapUiSettings(
                isZoomControlEnabled = false,
                isScaleBarEnabled = false,
                isLogoClickEnabled = false,
                isCompassEnabled = false,
            ),
        cameraPositionState = cameraPositionState,
        locationSource = rememberFusedLocationSource(),
        properties = MapProperties(locationTrackingMode = LocationTrackingMode.Follow),
        onLocationChange = onLocationChange,
    ) {
        if (markerLongitude != 0.0 && markerLatitude != 0.0) {
            MarkerComposable(
                keys = arrayOf("location"),
                state = markerState,
                onClick = {
                    onUiAction(PlanWriteUiAction.OnShowPlaceInfoDialog(true))
                    true
                },
            ) {
                Icon(
                    modifier = Modifier.size(54.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_location),
                    contentDescription = "",
                    tint = MoimTheme.colors.primary.primary,
                )
            }
        }
    }
}
