package com.moim.feature.intro.screen.signup.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimPrimaryButton
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.MoimTextField
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.moimButtomColors
import com.moim.core.designsystem.theme.moimTextFieldColors
import com.moim.feature.intro.screen.signup.OnSignUpUiAction
import com.moim.feature.intro.screen.signup.SignUpUiAction

@Composable
fun NicknameTextField(
    nickname: String = "",
    isDuplicated: Boolean? = false,
    isRegexError: Boolean = false,
    onUiAction: OnSignUpUiAction = {},
) {
    MoimText(
        text = stringResource(R.string.sign_up_nickname),
        style = MoimTheme.typography.title03.semiBold,
        color = MoimTheme.colors.text.text01,
    )

    Spacer(Modifier.height(8.dp))

    MoimTextField(
        hintText = stringResource(R.string.sign_up_nickname_hint),
        textFieldColors =
            moimTextFieldColors().copy(
                focusedSupportingTextColor = Color(0xff34C759),
                unfocusedSupportingTextColor = Color(0xff34C759),
            ),
        isError = isDuplicated == true || isRegexError,
        errorMessage = stringResource(if (isRegexError) R.string.sign_up_regex_error else R.string.sign_up_duplicate_error),
        supportText = if (isDuplicated == false) stringResource(R.string.sign_up_duplicate_pass) else null,
        text = nickname,
        textMaxLength = 12,
        onTextChanged = { onUiAction(SignUpUiAction.OnChangeNickname(it)) },
        trailingIcon = {
            Box(
                modifier = Modifier.padding(end = 16.dp),
            ) {
                MoimPrimaryButton(
                    modifier = Modifier,
                    buttonColors = moimButtomColors().copy(containerColor = MoimTheme.colors.secondary),
                    verticalPadding = 8.dp,
                    text = stringResource(R.string.sign_up_duplicate_check),
                    style = MoimTheme.typography.body01.semiBold,
                    onClick = { onUiAction(SignUpUiAction.OnClickDuplicatedCheck) },
                )
            }
        },
    )
}
