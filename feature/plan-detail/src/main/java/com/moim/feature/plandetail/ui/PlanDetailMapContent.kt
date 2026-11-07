package com.moim.feature.plandetail.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.plandetail.model.PlanDetailIntent
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.NaverMap.OnMapRenderedListener
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapEffect
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.MarkerComposable
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberUpdatedMarkerState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlin.time.Duration.Companion.milliseconds

private const val SNAPSHOT_DEBOUNCE_MILLIS = 500L

// SurfaceView 기반 지도는 전환 애니메이션·스크롤 재사용에 취약해 로드 후 스냅샷 이미지로 대체
@Composable
fun PlanDetailMapContent(
    modifier: Modifier = Modifier,
    latitude: Double,
    longitude: Double,
    snapshot: ImageBitmap?,
    onSnapshot: (ImageBitmap) -> Unit,
    onIntent: (PlanDetailIntent) -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .border(BorderStroke(1.dp, MoimTheme.colors.stroke))
                .aspectRatio(33f / 16f),
    ) {
        if (snapshot != null) {
            Image(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .onSingleClick { onIntent(PlanDetailIntent.MapDetailClick) },
                bitmap = snapshot,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        } else {
            PlanDetailNaverMap(
                latitude = latitude,
                longitude = longitude,
                onSnapshot = onSnapshot,
                onIntent = onIntent,
            )
        }
    }
}

@OptIn(ExperimentalNaverMapApi::class, FlowPreview::class)
@Composable
private fun PlanDetailNaverMap(
    latitude: Double,
    longitude: Double,
    onSnapshot: (ImageBitmap) -> Unit,
    onIntent: (PlanDetailIntent) -> Unit,
) {
    val cameraPositionState =
        rememberCameraPositionState(key = "$latitude,$longitude") {
            position = CameraPosition(LatLng(latitude, longitude), 15.2)
        }
    val markerState = rememberUpdatedMarkerState(position = LatLng(latitude, longitude))

    NaverMap(
        uiSettings =
            MapUiSettings(
                isScrollGesturesEnabled = false,
                isZoomGesturesEnabled = false,
                isTiltGesturesEnabled = false,
                isRotateGesturesEnabled = false,
                isStopGesturesEnabled = false,
                isZoomControlEnabled = false,
                isCompassEnabled = false,
                isScaleBarEnabled = false,
                isLocationButtonEnabled = false,
                isLogoClickEnabled = false,
            ),
        onMapClick = { _, _ -> onIntent(PlanDetailIntent.MapDetailClick) },
        cameraPositionState = cameraPositionState,
    ) {
        MarkerComposable(
            keys = arrayOf("location"),
            state = markerState,
        ) {
            Icon(
                modifier = Modifier.size(54.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_location),
                contentDescription = "",
                tint = MoimTheme.colors.global.primary,
            )
        }

        MapEffect(Unit) { naverMap ->
            val renderedFlow = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
            val listener = OnMapRenderedListener { fully, _ -> renderedFlow.tryEmit(fully) }
            naverMap.addOnMapRenderedListener(listener)
            try {
                // 라벨·아이콘 배치까지 끝나 렌더링이 잠잠해진 뒤 촬영
                renderedFlow.debounce(SNAPSHOT_DEBOUNCE_MILLIS.milliseconds).first { it }
                naverMap.takeSnapshot(false) { bitmap -> onSnapshot(bitmap.asImageBitmap()) }
            } finally {
                naverMap.removeOnMapRenderedListener(listener)
            }
        }
    }
}
