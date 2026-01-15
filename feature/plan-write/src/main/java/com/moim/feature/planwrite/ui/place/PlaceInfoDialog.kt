package com.moim.feature.planwrite.ui.place

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
import com.moim.core.common.model.Place
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimBottomSheetDialog
import com.moim.core.designsystem.component.MoimPrimaryButton
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.planwrite.PlanWriteUiAction
import kotlinx.coroutines.launch

@Composable
fun PlaceInfoDialog(
    modifier: Modifier = Modifier,
    place: Place,
    onUiAction: (PlanWriteUiAction) -> Unit,
) {
    val dismissAction = PlanWriteUiAction.OnShowPlaceInfoDialog(false)
    val sheetState: SheetState = rememberModalBottomSheetState(true)
    val coroutineScope = rememberCoroutineScope()

    MoimBottomSheetDialog(
        modifier = modifier,
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
                text = place.title,
                style = MoimTheme.typography.title02.semiBold,
                color = MoimTheme.colors.gray.gray02,
            )
            Spacer(Modifier.height(16.dp))

            MoimText(
                text = place.roadAddress,
                singleLine = false,
                style = MoimTheme.typography.body01.regular,
                color = MoimTheme.colors.gray.gray05,
            )

            Spacer(Modifier.height(20.dp))

            MoimPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onUiAction(PlanWriteUiAction.OnClickPlanPlace(place))
                    onUiAction(dismissAction)
                },
            ) {
                Text(
                    text = stringResource(R.string.plan_write_place_select),
                    style = MoimTheme.typography.body01.semiBold,
                )
            }
        }
    }
}
