package com.moim.feature.mapdetail.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimBottomSheetDialog
import com.moim.core.designsystem.component.MoimPrimaryButton
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.moimButtomColors
import com.moim.feature.mapdetail.MapDetailUiAction
import kotlinx.coroutines.launch

@Composable
fun MapDetailPlaceInfoDialog(
    modifier: Modifier = Modifier,
    placeName: String,
    address: String,
    onUiAction: (MapDetailUiAction) -> Unit,
) {
    val dismissAction = MapDetailUiAction.OnShowPlaceInfoDialog(false)
    val sheetState: SheetState = rememberModalBottomSheetState(true)
    val coroutineScope = rememberCoroutineScope()

    MoimBottomSheetDialog(
        modifier = modifier.fillMaxWidth(),
        onDismiss = {
            coroutineScope
                .launch { sheetState.hide() }
                .invokeOnCompletion { onUiAction(dismissAction) }
        },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            MoimText(
                text = placeName,
                style = MoimTheme.typography.title02.semiBold,
                color = MoimTheme.colors.text.text01,
            )
            Spacer(Modifier.height(16.dp))

            MoimText(
                text = address,
                singleLine = false,
                style = MoimTheme.typography.body01.regular,
                color = MoimTheme.colors.text.text04,
            )

            Spacer(Modifier.height(20.dp))

            MoimPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                buttonColors =
                    moimButtomColors().copy(
                        containerColor = MoimTheme.colors.tertiary,
                        contentColor = MoimTheme.colors.text.text01,
                    ),
                onClick = {
                    onUiAction(MapDetailUiAction.OnShowMapAppDialog(true))
                    onUiAction(dismissAction)
                },
            ) {
                Text(
                    text = stringResource(R.string.map_detail_find_location),
                    style = MoimTheme.typography.title03.semiBold,
                )
            }
        }
    }
}
