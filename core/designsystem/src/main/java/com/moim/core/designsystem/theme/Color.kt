package com.moim.core.designsystem.theme

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val color_000000 = Color(0xFF000000)
val color_222222 = Color(0xFF222222)
val color_333333 = Color(0xFF333333)
val color_3E3F40 = Color(0xff3E3F40)
val color_555555 = Color(0XFF555555)
val color_888888 = Color(0xFF888888)
val color_999999 = Color(0xFF999999)
val color_CCCCCC = Color(0xFFCCCCCC)
val color_E2E5E9 = Color(0XFFE2E5E9)
val color_DEE0E3 = Color(0XFFDEE0E3)
val color_D9D9D9 = Color(0XFFD9D9D9)
val color_DCDCDC = Color(0XFFDCDCDC)
val color_F5F5F5 = Color(0xFFF5F5F5)
val color_F1F2F3 = Color(0xFFF1F2F3)
val color_F6F8FA = Color(0xFFF6F8FA)
val color_F2F2F2 = Color(0XFFF2F2F2)
val color_FFFFFF = Color(0xFFFFFFFF)

val color_FF3B30 = Color(0XFFFF3B30)
val color_34C759 = Color(0XFF34C759)
val color_FEE500 = Color(0xFFFEE500)
val color_3366FF = Color(0xFF3366FF)
val color_D6E0FF = Color(0xFFD6E0FF)
val color_EBF0FF = Color(0xFFEBF0FF)

val LocalMoimColors = staticCompositionLocalOf {
    MoimColor()
}

@Immutable
data class MoimColor(
    val primary: Primary = Primary(),
    val bg: Background = Background(),
    val input: Input = Input(),
    val gray: Gray = Gray(),
    val secondary: Color = color_3E3F40,
    val tertiary: Color = color_F1F2F3,
    val black: Color = color_000000,
    val white: Color = color_FFFFFF,
    val red: Color = color_FF3B30,
    val stroke: Color = color_F2F2F2,
    val icon: Color = color_D9D9D9,
    val blueGray: Color = color_EBF0FF,
)

@Immutable
data class Background(
    val primary: Color = color_F5F5F5,
    val input: Color = color_F6F8FA,
)

@Immutable
data class Primary(
    val primary: Color = color_3366FF,
    val disable: Color = color_D6E0FF
)

@Immutable
data class Input(
    val icon: Color = color_DEE0E3,
    val disable: Color = color_E2E5E9
)

@Immutable
data class Gray(
    val gray01: Color = color_222222,
    val gray02: Color = color_333333,
    val gray03: Color = color_555555,
    val gray04: Color = color_888888,
    val gray05: Color = color_999999,
    val gray06: Color = color_CCCCCC,
    val gray07: Color = color_DCDCDC,
)

@Composable
fun moimTextFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = MoimTheme.colors.bg.input,
    unfocusedContainerColor = MoimTheme.colors.bg.input,
    errorContainerColor = MoimTheme.colors.bg.input,

    focusedIndicatorColor = MoimTheme.colors.bg.input,
    unfocusedIndicatorColor = MoimTheme.colors.bg.input,
    errorIndicatorColor = MoimTheme.colors.bg.input,

    focusedTextColor = MoimTheme.colors.gray.gray01,
    unfocusedTextColor = MoimTheme.colors.gray.gray01,
    errorTextColor = MoimTheme.colors.gray.gray01,

    focusedSupportingTextColor = MoimTheme.colors.gray.gray01,
    unfocusedSupportingTextColor = MoimTheme.colors.gray.gray01,
    errorSupportingTextColor = MoimTheme.colors.red,

    focusedLeadingIconColor = MoimTheme.colors.gray.gray01,
    unfocusedLeadingIconColor = MoimTheme.colors.gray.gray01,
    errorLeadingIconColor = MoimTheme.colors.gray.gray01,

    focusedTrailingIconColor = MoimTheme.colors.gray.gray01,
    unfocusedTrailingIconColor = MoimTheme.colors.gray.gray01,
    errorTrailingIconColor = MoimTheme.colors.gray.gray01,

    focusedPlaceholderColor = MoimTheme.colors.gray.gray05,
    unfocusedPlaceholderColor = MoimTheme.colors.gray.gray05,
    errorPlaceholderColor = MoimTheme.colors.gray.gray05,

    cursorColor = MoimTheme.colors.primary.primary,
    errorCursorColor = MoimTheme.colors.primary.primary,
)

@Composable
fun moimButtomColors() = ButtonDefaults.buttonColors(
    containerColor = MoimTheme.colors.primary.primary,
    contentColor = MoimTheme.colors.white,
    disabledContainerColor = MoimTheme.colors.primary.disable,
    disabledContentColor = MoimTheme.colors.white,
)