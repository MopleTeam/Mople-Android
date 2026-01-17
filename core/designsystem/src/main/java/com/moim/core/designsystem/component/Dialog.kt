package com.moim.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.moim.core.designsystem.R
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.moimButtomColors

@Composable
fun MoimDialog(
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    usePlatformDefaultWidth: Boolean = true,
    decorFitsSystemWindows: Boolean = true,
    onDismiss: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        properties =
            DialogProperties(
                dismissOnBackPress = dismissOnBackPress,
                dismissOnClickOutside = dismissOnClickOutside,
                usePlatformDefaultWidth = usePlatformDefaultWidth,
                decorFitsSystemWindows = decorFitsSystemWindows,
            ),
        onDismissRequest = onDismiss,
        content = {
            MoimTheme {
                content()
            }
        },
    )
}

@Composable
fun MoimAlertDialog(
    title: String = "",
    description: String = "",
    isNegative: Boolean = true,
    cancelable: Boolean = true,
    negativeText: String = stringResource(id = R.string.common_negative),
    positiveText: String = stringResource(id = R.string.common_positive),
    positiveButtonColors: ButtonColors = moimButtomColors(),
    onClickNegative: () -> Unit = {},
    onClickPositive: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = cancelable, dismissOnClickOutside = cancelable),
        content = {
            MoimTheme {
                Column(
                    modifier =
                        Modifier
                            .background(MoimTheme.colors.bg.primary, RoundedCornerShape(10.dp))
                            .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp)
                            .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    MoimText(
                        text = title,
                        singleLine = false,
                        style = MoimTheme.typography.title02.semiBold,
                        color = MoimTheme.colors.text.text01,
                    )

                    if (description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        MoimText(
                            text = description,
                            singleLine = false,
                            style = MoimTheme.typography.body01.regular,
                            color = MoimTheme.colors.text.text02,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (isNegative) {
                            MoimPrimaryButton(
                                modifier = Modifier.weight(1f),
                                onClick = onClickNegative,
                                verticalPadding = 16.dp,
                                text = negativeText,
                                style = MoimTheme.typography.title03.semiBold,
                                buttonColors =
                                    moimButtomColors().copy(
                                        containerColor = MoimTheme.colors.tertiary,
                                        contentColor = MoimTheme.colors.text.text01,
                                    ),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        MoimPrimaryButton(
                            modifier = Modifier.weight(1f),
                            onClick = onClickPositive,
                            verticalPadding = 16.dp,
                            text = positiveText,
                            buttonColors = positiveButtonColors,
                            style = MoimTheme.typography.title03.semiBold,
                        )
                    }
                }
            }
        },
    )
}

@Preview
@Composable
private fun MoimAlertDialogPreview() {
    Column(
        modifier = Modifier.containerScreen(),
    ) {
        MoimAlertDialog(
            title = "정말 탈퇴하시겠어요?",
            description = "회원 탈퇴하면 모임과 일정을 복구할 수 없어요",
        )
    }
}
