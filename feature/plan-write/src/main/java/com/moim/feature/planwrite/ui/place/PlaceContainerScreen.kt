package com.moim.feature.planwrite.ui.place

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimTextField
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.model.Place
import com.moim.feature.planwrite.OnPlanWriteUiAction
import com.moim.feature.planwrite.PlanWriteUiAction

@Composable
fun PlaceContainerScreen(
    modifier: Modifier = Modifier,
    searchPlaces: List<Place> = emptyList(),
    selectedPlace: Place? = null,
    isSearchList: Boolean = false,
    onUiAction: OnPlanWriteUiAction,
) {
    var currentX by remember { mutableDoubleStateOf(0.0) }
    var currentY by remember { mutableDoubleStateOf(0.0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        MapTopAppbar(
            modifier = Modifier.fillMaxWidth(),
            onClickBack = { onUiAction(PlanWriteUiAction.OnClickBack) },
            onClickSearch = {
                onUiAction(
                    PlanWriteUiAction.OnClickPlanPlaceSearch(
                        keyword = it,
                        xPoint = currentX.toString(),
                        yPoint = currentY.toString()
                    )
                )
            }
        )

        Box {
            PlaceMapScreen(
                x = currentX,
                y = currentY,
                onLocationChange = {
                    if (currentX == 0.0 && currentY == 0.0) {
                        currentX = it.longitude
                        currentY = it.latitude
                    }
                }
            )

            if (isSearchList) {
                PlaceSearchScreen(
                    searchPlaces = searchPlaces,
                    selectedPlace = selectedPlace,
                    onUiAction = onUiAction
                )
            }
        }
    }
}

@Composable
fun MapTopAppbar(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickSearch: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    MoimTopAppbar(
        modifier = modifier,
        title = {
            MoimTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                hintText = stringResource(R.string.plan_write_place_search_hint),
                text = searchText,
                shape = RoundedCornerShape(8.dp),
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_search),
                        contentDescription = ""
                    )
                },
                onTextChanged = { searchText = it }
            )
        },
        onClickNavigate = onClickBack,
        actions = {
            AnimatedVisibility(
                visible = searchText.isNotEmpty()
            ) {
                TextButton(
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        onClickSearch(searchText)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.common_confirm),
                        style = MoimTheme.typography.body01.semiBold,
                        color = MoimTheme.colors.gray.gray01
                    )
                }
            }
        }
    )
}