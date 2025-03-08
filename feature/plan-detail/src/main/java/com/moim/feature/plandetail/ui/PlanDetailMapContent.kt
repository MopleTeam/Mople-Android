package com.moim.feature.plandetail.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.theme.MoimTheme
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
import com.naver.maps.map.compose.rememberMarkerState

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun PlanDetailMapContent(
    modifier: Modifier = Modifier,
    latitude: Double,
    longitude: Double
) {
    val cameraPositionState = rememberCameraPositionState().apply {
        position = CameraPosition(LatLng(latitude, longitude), 15.2)
    }
    val markerState = rememberMarkerState(position = LatLng(latitude, longitude))

    NaverMap(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .border(BorderStroke(1.dp, MoimTheme.colors.stroke))
            .aspectRatio(33f / 16f),
        uiSettings = MapUiSettings(
            isZoomControlEnabled = false,
            isScaleBarEnabled = false,
            isLogoClickEnabled = false,
            isCompassEnabled = false,
        ),
        cameraPositionState = cameraPositionState,
        locationSource = rememberFusedLocationSource(),
        properties = MapProperties(locationTrackingMode = LocationTrackingMode.Follow),
        onLocationChange = {}
    ) {
        MarkerComposable(
            keys = arrayOf("location"),
            state = markerState,
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