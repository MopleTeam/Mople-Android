package com.moim.core.designsystem.component.internal

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.theme.MoimTheme
import timber.log.Timber


internal val OutlinedTextFieldTopPadding = 8.dp

@Composable
internal fun MoimOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    paddingValues: PaddingValues = PaddingValues(16.dp),
    minWidth: Dp = OutlinedTextFieldDefaults.MinWidth,
    minHeight: Dp = OutlinedTextFieldDefaults.MinHeight,
    focusedBorderThickness: Dp = 2.dp,
    unfocusedBorderThickness: Dp = 1.dp,
) {
    val textSelectionColor = TextSelectionColors(
        handleColor = MoimTheme.colors.primary.primary,
        backgroundColor = MoimTheme.colors.gray.gray04
    )

    CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColor) {
        Column {
            BasicTextField(
                value = value,
                modifier = if (label != null) {
                    modifier
                        // Merge semantics at the beginning of the modifier chain to ensure padding is
                        // considered part of the text field.
                        .semantics(mergeDescendants = true) {}
                        .padding(top = OutlinedTextFieldTopPadding)
                } else {
                    modifier
                }.defaultMinSize(
                    minWidth = minWidth,
                    minHeight = minHeight
                ),
                onValueChange = onValueChange,
                enabled = enabled,
                readOnly = readOnly,
                textStyle = textStyle.copy(color = colors.focusedTextColor),
                cursorBrush = SolidColor(colors.cursorColor),
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                interactionSource = interactionSource,
                singleLine = singleLine,
                maxLines = maxLines,
                minLines = minLines,
                decorationBox = @Composable { innerTextField ->
                    OutlinedTextFieldDefaults.DecorationBox(
                        value = value,
                        visualTransformation = visualTransformation,
                        innerTextField = innerTextField,
                        placeholder = placeholder,
                        label = label,
                        leadingIcon = leadingIcon,
                        trailingIcon = trailingIcon,
                        prefix = prefix,
                        suffix = suffix,
                        singleLine = singleLine,
                        enabled = enabled,
                        isError = isError,
                        interactionSource = interactionSource,
                        colors = colors,
                        contentPadding = paddingValues,
                        container = {
                            OutlinedTextFieldDefaults.Container(
                                enabled = enabled,
                                isError = isError,
                                interactionSource = interactionSource,
                                colors = colors,
                                shape = shape,
                                focusedBorderThickness = focusedBorderThickness,
                                unfocusedBorderThickness = unfocusedBorderThickness
                            )
                        },
                    )
                }
            )

            if (supportingText != null) {
                Text(
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                    text = supportingText,
                    style = MoimTheme.typography.body01.regular,
                    color = if (isError) colors.errorSupportingTextColor else colors.focusedSupportingTextColor
                )
            }
        }
    }
}

@Composable
internal fun MoimOutlinedTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    paddingValues: PaddingValues = PaddingValues(16.dp),
    minWidth: Dp = OutlinedTextFieldDefaults.MinWidth,
    minHeight: Dp = OutlinedTextFieldDefaults.MinHeight,
    focusedBorderThickness: Dp = 2.dp,
    unfocusedBorderThickness: Dp = 1.dp,
) {
    val textSelectionColor = TextSelectionColors(
        handleColor = MoimTheme.colors.primary.primary,
        backgroundColor = MoimTheme.colors.gray.gray04
    )

    CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColor) {
        Column {
            BasicTextField(
                value = value,
                modifier = if (label != null) {
                    modifier
                        // Merge semantics at the beginning of the modifier chain to ensure padding is
                        // considered part of the text field.
                        .semantics(mergeDescendants = true) {}
                        .padding(top = OutlinedTextFieldTopPadding)
                } else {
                    modifier
                }.defaultMinSize(
                    minWidth = minWidth,
                    minHeight = minHeight
                ),
                onValueChange = onValueChange,
                enabled = enabled,
                readOnly = readOnly,
                textStyle = textStyle.copy(color = colors.focusedTextColor),
                cursorBrush = SolidColor(colors.cursorColor),
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                interactionSource = interactionSource,
                singleLine = singleLine,
                maxLines = maxLines,
                minLines = minLines,
                decorationBox = @Composable { innerTextField ->
                    OutlinedTextFieldDefaults.DecorationBox(
                        value = value.text,
                        visualTransformation = visualTransformation,
                        innerTextField = innerTextField,
                        placeholder = placeholder,
                        label = label,
                        leadingIcon = leadingIcon,
                        trailingIcon = trailingIcon,
                        prefix = prefix,
                        suffix = suffix,
                        singleLine = singleLine,
                        enabled = enabled,
                        isError = isError,
                        interactionSource = interactionSource,
                        colors = colors,
                        contentPadding = paddingValues,
                        container = {
                            OutlinedTextFieldDefaults.ContainerBox(
                                enabled,
                                isError,
                                interactionSource,
                                colors,
                                shape,
                                focusedBorderThickness,
                                unfocusedBorderThickness
                            )
                        },
                    )
                }
            )

            if (supportingText != null) {
                Text(
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                    text = supportingText,
                    style = MoimTheme.typography.body01.regular,
                    color = if (isError) colors.errorSupportingTextColor else colors.focusedSupportingTextColor
                )
            }
        }
    }
}