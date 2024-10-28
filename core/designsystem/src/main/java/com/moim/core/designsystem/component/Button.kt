package com.moim.core.designsystem.component

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.color_3366FF
import com.moim.core.designsystem.theme.color_FFFFFF

@Composable
fun MoimPrimaryButton(
    modifier: Modifier = Modifier,
    defaultMinHeight: Dp = 56.dp,
    shapes: RoundedCornerShape = RoundedCornerShape(8.dp),
    enable: Boolean = true,
    text: String = "",
    style: TextStyle = MoimTheme.typography.bold16,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier.defaultMinSize(minHeight = defaultMinHeight),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = color_3366FF,
            contentColor = color_FFFFFF
        ),
        enabled = enable,
        shape = shapes,
        content = {
            Text(
                text = text,
                style = style
            )
        }
    )
}