package com.moim.core.designsystem.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
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
    singleLine: Boolean = true,
    minLine: Int = 1,
    maxLine: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
) {
    val limitLine = if (singleLine) minLine else maxLine

    Text(
        modifier = modifier,
        text = text,
        textAlign = textAlign,
        style = style,
        color = color,
        minLines = minLine,
        maxLines = limitLine,
        overflow = overflow,
    )
}

@Composable
fun MoimText(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    textAlign: TextAlign? = null,
    style: TextStyle = MoimTheme.typography.body01.medium,
    color: Color = MoimTheme.colors.gray.gray01,
    singleLine: Boolean = true,
    minLine: Int = 1,
    maxLine: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
) {
    val limitLine = if (singleLine) minLine else maxLine

    Text(
        modifier = modifier,
        text = text,
        textAlign = textAlign,
        style = style,
        color = color,
        minLines = minLine,
        maxLines = limitLine,
        overflow = overflow,
    )
}
