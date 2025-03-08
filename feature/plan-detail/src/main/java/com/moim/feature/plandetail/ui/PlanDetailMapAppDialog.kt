package com.moim.feature.plandetail.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimBottomSheetDialog
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.model.MapType
import com.moim.feature.plandetail.OnPlanDetailUiAction
import com.moim.feature.plandetail.PlanDetailUiAction
import kotlinx.coroutines.launch

@Composable
fun PlanDetailMapAppDialog(
    modifier: Modifier = Modifier,
    onUiAction: OnPlanDetailUiAction
) {
    val dismissAction = PlanDetailUiAction.OnShowMapAppDialog(false)
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MoimText(
                text = stringResource(R.string.plan_detail_map_title),
                style = MoimTheme.typography.title01.bold,
                color = MoimTheme.colors.gray.gray01
            )
            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MapLogo(
                    appLogoRes = R.drawable.img_map_app_logo_for_naver,
                    appName = stringResource(R.string.plan_detail_map_for_naver),
                    onClick = {
                        onUiAction(PlanDetailUiAction.OnClickMapAddress(MapType.NAVER))
                        onUiAction(dismissAction)
                    }
                )

                MapLogo(
                    appLogoRes = R.drawable.img_map_app_logo_for_kakao,
                    appName = stringResource(R.string.plan_detail_map_for_kakao),
                    onClick = {
                        onUiAction(PlanDetailUiAction.OnClickMapAddress(MapType.KAKAO))
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
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .onSingleClick(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(BorderStroke(1.dp, MoimTheme.colors.stroke), RoundedCornerShape(6.dp)),
            painter = painterResource(appLogoRes),
            contentDescription = ""
        )

        Spacer(Modifier.height(4.dp))

        MoimText(
            text = appName,
            style = MoimTheme.typography.body01.semiBold,
            color = MoimTheme.colors.gray.gray01
        )
    }
}