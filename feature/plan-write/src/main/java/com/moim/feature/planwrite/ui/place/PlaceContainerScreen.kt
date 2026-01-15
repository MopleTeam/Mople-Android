package com.moim.feature.planwrite.ui.place

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moim.core.common.model.Place
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimPrimaryButton
import com.moim.core.designsystem.component.MoimTextField
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.planwrite.OnPlanWriteUiAction
import com.moim.feature.planwrite.PlanWriteUiAction

@Composable
fun PlaceContainerScreen(
    modifier: Modifier = Modifier,
    searchPlaces: List<Place> = emptyList(),
    searchKeyword: String? = null,
    planLongitude: Double?,
    planLatitude: Double?,
    selectedPlace: Place? = null,
    isShowSearchScreen: Boolean = false,
    isShowPlaceInfoDialog: Boolean = false,
    onUiAction: OnPlanWriteUiAction,
) {
    var currentX by remember { mutableDoubleStateOf(0.0) }
    var currentY by remember { mutableDoubleStateOf(0.0) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .systemBarsPadding(),
    ) {
        MapTopAppbar(
            modifier = Modifier.fillMaxWidth(),
            onClickBack = { onUiAction(PlanWriteUiAction.OnClickBack) },
            onClickSearch = {
                onUiAction(
                    PlanWriteUiAction.OnClickPlanPlaceSearch(
                        keyword = it,
                        xPoint = currentX.toString(),
                        yPoint = currentY.toString(),
                    ),
                )
            },
        )

        Box {
            PlaceMapScreen(
                longitude = currentX,
                latitude = currentY,
                markerLongitude = planLongitude,
                markerLatitude = planLatitude,
                onUiAction = onUiAction,
                onLocationChange = {
                    if (currentX == 0.0 && currentY == 0.0) {
                        currentX = it.longitude
                        currentY = it.latitude
                    }
                },
            )

            if (isShowSearchScreen) {
                PlaceSearchScreen(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .imePadding()
                            .background(MoimTheme.colors.white),
                    isSearchResult = searchKeyword.isNullOrEmpty().not(),
                    searchPlaces = searchPlaces,
                    selectedPlace = selectedPlace,
                    onUiAction = onUiAction,
                )
            }

            if (isShowPlaceInfoDialog && selectedPlace != null) {
                PlaceInfoDialog(
                    place = selectedPlace,
                    onUiAction = onUiAction,
                )
            }
        }
    }
}

@Composable
fun MapTopAppbar(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickSearch: (String) -> Unit,
) {
    var searchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    MoimTopAppbar(
        modifier = modifier,
        title = {
            MoimTextField(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                hintText = stringResource(R.string.plan_write_place_search_hint),
                text = searchText,
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    Box(
                        modifier =
                            Modifier
                                .padding(end = 16.dp)
                                .padding(vertical = 8.dp),
                    ) {
                        MoimPrimaryButton(
                            verticalPadding = 8.dp,
                            enable = searchText.isNotBlank(),
                            onClick = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                onClickSearch(searchText)
                            },
                        ) {
                            Text(
                                text = stringResource(R.string.common_search),
                                style = MoimTheme.typography.body01.semiBold,
                            )
                        }
                    }
                },
                onTextChanged = { searchText = it },
            )
        },
        onClickNavigate = onClickBack,
    )
}
