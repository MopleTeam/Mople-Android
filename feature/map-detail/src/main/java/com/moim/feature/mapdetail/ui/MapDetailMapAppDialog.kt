package com.moim.feature.mapdetail.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moim.core.common.model.MapType
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimBottomSheetDialog
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.mapdetail.MapDetailUiAction
import kotlinx.coroutines.launch

@Composable
fun MapDetailMapAppDialog(
    modifier: Modifier = Modifier,
    onUiAction: (MapDetailUiAction) -> Unit,
) {
    val dismissAction = MapDetailUiAction.OnShowMapAppDialog(false)
    val sheetState: SheetState = rememberModalBottomSheetState(true)
    val coroutineScope = rememberCoroutineScope()

    MoimBottomSheetDialog(
        modifier = modifier.fillMaxWidth(),
        dragHandle = {
            Spacer(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .size(width = 80.dp, height = 8.dp)
                    .clip(CircleShape)
                    .background(MoimTheme.colors.tertiary)
            )
        },
        onDismiss = {
            coroutineScope
                .launch { sheetState.hide() }
                .invokeOnCompletion { onUiAction(dismissAction) }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                MapLogo(
                    appLogoRes = R.drawable.img_map_app_logo_for_naver,
                    appName = stringResource(R.string.map_detail_map_for_naver),
                    onClick = {
                        onUiAction(MapDetailUiAction.OnClickMapAddress(MapType.NAVER))
                        onUiAction(dismissAction)
                    }
                )

                MapLogo(
                    appLogoRes = R.drawable.img_map_app_logo_for_kakao,
                    appName = stringResource(R.string.map_detail_map_for_kakao),
                    onClick = {
                        onUiAction(MapDetailUiAction.OnClickMapAddress(MapType.KAKAO))
                        onUiAction(dismissAction)
                    }
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MapLogo(
    @DrawableRes appLogoRes: Int,
    appName: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .onSingleClick(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .border(BorderStroke(1.dp, MoimTheme.colors.stroke), CircleShape),
            painter = painterResource(appLogoRes),
            contentDescription = ""
        )

        Spacer(Modifier.size(8.dp))

        MoimText(
            text = appName,
            style = MoimTheme.typography.body01.medium,
            color = MoimTheme.colors.gray.gray02
        )
    }
}