package com.moim.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.moim.core.designsystem.R
import com.moim.core.designsystem.theme.MoimTheme

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
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside,
            usePlatformDefaultWidth = usePlatformDefaultWidth,
            decorFitsSystemWindows = decorFitsSystemWindows
        ),
        onDismissRequest = onDismiss,
        content = {
            MoimTheme {
                content()
            }
        }
    )
}

@Composable
fun MoimAlertDialog(
    title: String = "",
    description: String = "",
    isNegative: Boolean = true,
    cancelable: Boolean = true,
    negativeText: String = stringResource(id = R.string.common_cancel),
    positiveText: String = stringResource(id = R.string.common_confirm),
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
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        style = MoimTheme.typography.bold16,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = description,
                        style = MoimTheme.typography.regular14,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))

                        if (isNegative) {
                            TextButton(
                                onClick = onClickNegative
                            ) {
                                Text(
                                    text = negativeText,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MoimTheme.typography.bold16,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                        MoimPrimaryButton(
                            onClick = onClickPositive,
                            defaultMinHeight = 40.dp,
                            text = positiveText,
                        )
                    }
                }
            }
        }
    )
}
