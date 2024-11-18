package com.moim.feature.planwrite.ui.place

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.model.Place
import com.moim.feature.planwrite.OnPlanWriteUiAction
import com.moim.feature.planwrite.PlanWriteUiAction

@Composable
fun PlaceSearchScreen(
    modifier: Modifier = Modifier,
    searchPlaces: List<Place>,
    selectedPlace: Place? = null,
    onUiAction: OnPlanWriteUiAction = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MoimTheme.colors.white),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(
            items = searchPlaces,
            key = { it.roadAddress }
        ) {
            PlaceItem(
                place = it,
                selectedPlace = selectedPlace,
                onUiAction = onUiAction
            )
        }
    }
}

@Composable
fun PlaceItem(
    modifier: Modifier = Modifier,
    place: Place,
    selectedPlace: Place? = null,
    onUiAction: OnPlanWriteUiAction = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(if (selectedPlace?.roadAddress == place.roadAddress) MoimTheme.colors.bg.primary else MoimTheme.colors.white)
            .onSingleClick { onUiAction(PlanWriteUiAction.OnClickPlanPlace(place)) }
            .padding(20.dp)
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_location),
            contentDescription = "",
            tint = MoimTheme.colors.icon
        )

        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = place.title,
                style = MoimTheme.typography.title03.medium,
                color = MoimTheme.colors.gray.gray01,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = place.roadAddress,
                style = MoimTheme.typography.body01.regular,
                color = MoimTheme.colors.gray.gray05,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
private fun PlaceItemPreview() {
    MoimTheme {
        PlaceSearchScreen(
            searchPlaces = listOf(
                Place(title = "시티극장", roadAddress = "서울 강남구 강남대로 422 지하 2층"),
                Place(title = "CGV 청담씨네마시티", roadAddress = "서울 강남구 도산대로 323 8층"),
                Place(title = "메가박스 센트럴", roadAddress = "서울 서초구 신반포로 176 센트럴시티빌딩 지하1층"),
            ),
            selectedPlace = Place(title = "메가박스 센트럴", roadAddress = "서울 서초구 신반포로 176 센트럴시티빌딩 지하1층"),
        )
    }
}