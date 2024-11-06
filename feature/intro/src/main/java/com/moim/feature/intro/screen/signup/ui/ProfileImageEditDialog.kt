package com.moim.feature.intro.screen.signup.ui

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimBottomSheetDialog
import com.moim.core.designsystem.component.MoimPrimaryButton
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.color_222222
import com.moim.core.designsystem.theme.color_3E3F40
import com.moim.core.designsystem.theme.color_888888
import com.moim.core.designsystem.theme.moimButtomColors
import com.moim.feature.intro.screen.signup.OnSignUpUiAction
import com.moim.feature.intro.screen.signup.SignUpUiAction
import kotlinx.coroutines.launch

@Composable
fun ProfileImageEditDialog(
    modifier: Modifier = Modifier,
    onUiAction: OnSignUpUiAction,
) {
    val dismissAction = SignUpUiAction.OnShowProfileEditDialog(false)
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.sign_up_profile_edit),
                style = MoimTheme.typography.body01.semiBold,
                color = color_222222
            )
            Spacer(Modifier.height(24.dp))

            MoimPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.sign_up_profile_default),
                buttonColors = moimButtomColors().copy(containerColor = color_888888),
                onClick = {
                    onUiAction(dismissAction)
                    onUiAction(SignUpUiAction.OnChangeProfileUrl(null))
                }
            )
            Spacer(Modifier.height(12.dp))

            MoimPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.sign_up_profile_photo),
                buttonColors = moimButtomColors().copy(containerColor = color_3E3F40),
                onClick = {
                    onUiAction(dismissAction)
                    onUiAction(SignUpUiAction.OnNavigatePhotoPicker)
                }
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}