package com.moim.feature.intro.screen.signup.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SheetState
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
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.theme.MoimTheme
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
            MoimText(
                text = stringResource(R.string.common_profile_edit),
                singleLine = false,
                style = MoimTheme.typography.body01.semiBold,
                color = MoimTheme.colors.gray.gray01
            )
            Spacer(Modifier.height(24.dp))

            MoimPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.common_default_select),
                buttonColors = moimButtomColors().copy(containerColor = MoimTheme.colors.gray.gray04),
                onClick = {
                    onUiAction(dismissAction)
                    onUiAction(SignUpUiAction.OnChangeProfileUrl(null))
                }
            )
            Spacer(Modifier.height(12.dp))

            MoimPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.common_album_select),
                buttonColors = moimButtomColors().copy(containerColor = MoimTheme.colors.secondary),
                onClick = {
                    onUiAction(dismissAction)
                    onUiAction(SignUpUiAction.OnNavigatePhotoPicker)
                }
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}