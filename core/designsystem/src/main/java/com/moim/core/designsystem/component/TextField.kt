package com.moim.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.component.internal.MoimOutlinedTextField
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.color_FFFFFF
import com.moim.core.designsystem.theme.moimTextFieldColors

@Composable
fun MoimTextField(
    modifier: Modifier = Modifier.fillMaxWidth(),
    hintText: String,
    text: String = "",
    textStyle: TextStyle = MoimTheme.typography.regular14,
    textMaxLength: Int = 100,
    textFieldColors: TextFieldColors = moimTextFieldColors(),
    onTextChanged: (String) -> Unit = {},
    supportText: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    imeAction: ImeAction = ImeAction.Done,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    maxLine: Int = 1,
    shape: Shape = RoundedCornerShape(6),
    paddingValues: PaddingValues = PaddingValues(16.dp),
    unfocusedBorderThickness: Dp = 1.dp,
    focusedBorderThickness: Dp = 2.dp,
    minWidth: Dp = OutlinedTextFieldDefaults.MinWidth,
    minHeight: Dp = OutlinedTextFieldDefaults.MinHeight,
    focusManager: FocusManager = LocalFocusManager.current,
    isClearFocus: Boolean = true,
) {
    val currentText by rememberUpdatedState(newValue = text)
    var dummyText by rememberSaveable { mutableStateOf(currentText) }

    val onTextTriggered = {
        if (isClearFocus) focusManager.clearFocus()
        onTextChanged(text)
    }

    if (currentText.isEmpty() && dummyText.isNotEmpty()) {
        dummyText = ""
    }

    MoimOutlinedTextField(
        modifier = modifier.onKeyEvent {
            val pressedEnter = (it.key == Key.Enter)
            if (pressedEnter) onTextTriggered()
            pressedEnter
        },
        colors = textFieldColors,
        value = dummyText,
        onValueChange = {
            dummyText = it.take(textMaxLength)
            onTextChanged(dummyText)
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        placeholder = {
            Text(
                text = hintText,
                style = textStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },

        visualTransformation = visualTransformation,
        textStyle = textStyle,
        isError = isError,
        shape = shape,

        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = keyboardType
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onTextChanged(dummyText)
                focusManager.clearFocus()
            }
        ),
        supportingText = if (isError && errorMessage != null) errorMessage else supportText,
        maxLines = maxLine,
        singleLine = singleLine,
        paddingValues = paddingValues,
        minWidth = minWidth,
        minHeight = minHeight,
        focusedBorderThickness = focusedBorderThickness,
        unfocusedBorderThickness = unfocusedBorderThickness,
    )
}

@Preview
@Composable
private fun MoimTextFieldPreview() {
    MoimTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color_FFFFFF)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MoimTextField(
                modifier = Modifier.fillMaxWidth(),
                hintText = "This is Basic",
                singleLine = false,
                supportText = "dummy support message",
                errorMessage = "dummy error message",
                isError = false
            )

            MoimTextField(
                hintText = "아이디",
                text = "Test-User",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = ""
                    )
                },
                trailingIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = ""
                        )
                    }
                },
                isError = false,
                supportText = "dummy support message",
                errorMessage = "dummy error message",
            )

            MoimTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 139.dp),
                hintText = "This is Multi Line",
                singleLine = false,
                supportText = "dummy support message",
                errorMessage = "dummy error message",
                isError = true
            )
        }
    }
}