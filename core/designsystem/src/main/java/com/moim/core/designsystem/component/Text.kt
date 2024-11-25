package com.moim.core.designsystem.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.moim.core.designsystem.theme.MoimTheme

@Composable
fun MoimText(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign? = null,
    style: TextStyle = MoimTheme.typography.body01.medium,
    color: Color = MoimTheme.colors.gray.gray01,
    minLine: Int = 1,
    maxLine: Int = 1,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    Text(
        modifier = modifier,
        text = text,
        textAlign = textAlign,
        style = style,
        color = color,
        minLines = minLine,
        maxLines = maxLine,
        overflow = overflow
    )
}