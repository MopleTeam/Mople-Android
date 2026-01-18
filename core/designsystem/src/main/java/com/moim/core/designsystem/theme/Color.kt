package com.moim.core.designsystem.theme

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Secondary
val color_3E3F40 = Color(0xFF3E3F40)
val color_D5D8DC = Color(0xFFD5D8DC)

// Stroke
val color_F2F2F2 = Color(0xFFF2F2F2)
val color_1F1F1F = Color(0xFF1F1F1F)

// Tertiary
val color_F1F2F3 = Color(0xFFF1F2F3)
val color_262626 = Color(0xFF262626)

// Icon
val color_D9D9D9 = Color(0xFFD9D9D9)
val color_333333 = Color(0xFF333333)

// BlueGray
val color_EBF0FF = Color(0xFFEBF0FF)
val color_192341 = Color(0xFF192341)

// Primary Disable
val color_D6E0FF = Color(0xFFD6E0FF)
val color_303954 = Color(0xFF303954)

// Common colors
val color_FFFFFF = Color(0xFFFFFFFF)
val color_000000 = Color(0xFF000000)
val color_222222 = Color(0xFF222222)
val color_555555 = Color(0xFF555555)
val color_888888 = Color(0xFF888888)
val color_999999 = Color(0xFF999999)
val color_CCCCCC = Color(0xFFCCCCCC)
val color_DCDCDC = Color(0xFFDCDCDC)
val color_171717 = Color(0xFF171717)
val color_F5F5F5 = Color(0xFFF5F5F5)
val color_121212 = Color(0xFF121212)
val color_F6F8FA = Color(0xFFF6F8FA)
val color_1A1E23 = Color(0xFF1A1E23)
val color_DEE0E3 = Color(0xFFDEE0E3)
val color_E2E5E9 = Color(0xFFE2E5E9)
val color_2B323B = Color(0xFF2B323B)
val color_3366FF = Color(0xFF3366FF)
val color_FF3B30 = Color(0xFFFF3B30)
val color_FEE500 = Color(0xFFFEE500)

@Immutable
data class Global(
    val primary: Color,
    val black: Color,
    val white : Color,
    val red: Color,
)

@Immutable
data class Primary(
    val text: Color,
    val grayWhite: Color,
    val disable: Color,
    val disableText: Color,
)

@Immutable
data class Text(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val text01: Color,
    val text02: Color,
    val text03: Color,
    val text04: Color,
    val text05: Color,
    val text06: Color,
)

@Immutable
data class Background(
    val primary: Color,
    val secondary: Color,
    val input: Color,
)

@Immutable
data class Input(
    val icon: Color,
    val disable: Color,
)

@Immutable
data class Gray(
    val gray01: Color,
    val gray02: Color,
    val gray03: Color,
    val gray04: Color,
    val gray05: Color,
    val gray06: Color,
    val gray07: Color,
)

@Immutable
data class MoimColor(
    val global: Global,
    val primary: Primary,
    val text: Text,
    val bg: Background,
    val input: Input,
    val gray: Gray,
    val secondary: Color,
    val tertiary: Color,
    val stroke: Color,
    val icon: Color,
    val blueGray: Color,
    val transparent: Color = Color.Transparent,
)

// Light theme
fun lightMoimColor() =
    MoimColor(
        global =
            Global(
                primary = color_3366FF,
                black = color_000000,
                white = color_FFFFFF,
                red = color_FF3B30,
            ),
        primary =
            Primary(
                text = color_FFFFFF,
                grayWhite = color_FFFFFF,
                disable = color_D6E0FF,
                disableText = color_FFFFFF,
            ),
        text =
            Text(
                primary = color_FFFFFF,
                secondary = color_FFFFFF,
                tertiary = color_222222,
                text01 = color_222222,
                text02 = color_555555,
                text03 = color_888888,
                text04 = color_999999,
                text05 = color_FFFFFF,
                text06 = color_222222,
            ),
        bg =
            Background(
                primary = color_FFFFFF,
                secondary = color_F5F5F5,
                input = color_F6F8FA,
            ),
        input =
            Input(
                icon = color_DEE0E3,
                disable = color_E2E5E9,
            ),
        gray =
            Gray(
                gray01 = color_222222,
                gray02 = color_333333,
                gray03 = color_555555,
                gray04 = color_888888,
                gray05 = color_999999,
                gray06 = color_CCCCCC,
                gray07 = color_DCDCDC,
            ),
        secondary = color_3E3F40,
        tertiary = color_F1F2F3,
        stroke = color_F2F2F2,
        icon = color_D9D9D9,
        blueGray = color_EBF0FF,
    )

// Dark theme
fun darkMoimColor() =
    MoimColor(
        global =
            Global(
                primary = color_3366FF,
                black = color_000000,
                white = color_FFFFFF,
                red = color_FF3B30,
            ),
        primary =
            Primary(
                text = color_FFFFFF,
                grayWhite = color_FFFFFF,
                disable = color_192341,
                disableText = color_303954,
            ),
        text =
            Text(
                primary = color_FFFFFF,
                secondary = color_222222,
                tertiary = color_FFFFFF,
                text01 = color_DCDCDC,
                text02 = color_CCCCCC,
                text03 = color_888888,
                text04 = color_555555,
                text05 = color_FFFFFF,
                text06 = color_222222,
            ),
        bg =
            Background(
                primary = color_171717,
                secondary = color_121212,
                input = color_1A1E23,
            ),
        input =
            Input(
                icon = color_DEE0E3,
                disable = color_2B323B,
            ),
        gray =
            Gray(
                gray01 = color_DCDCDC,
                gray02 = color_CCCCCC,
                gray03 = color_999999,
                gray04 = color_888888,
                gray05 = color_555555,
                gray06 = color_333333,
                gray07 = color_222222,
            ),
        secondary = color_D5D8DC,
        tertiary = color_262626,
        stroke = color_1F1F1F,
        icon = color_333333,
        blueGray = color_192341,
    )

val LocalMoimColors = staticCompositionLocalOf { lightMoimColor() }
val LocalIsDarkTheme = staticCompositionLocalOf { false }

@Composable
fun moimTextFieldColors() =
    TextFieldDefaults.colors(
        // container
        focusedContainerColor = MoimTheme.colors.bg.input,
        unfocusedContainerColor = MoimTheme.colors.bg.input,
        disabledContainerColor = MoimTheme.colors.input.disable,
        errorContainerColor = MoimTheme.colors.bg.input,
        // indicator
        focusedIndicatorColor = MoimTheme.colors.bg.input,
        unfocusedIndicatorColor = MoimTheme.colors.bg.input,
        disabledIndicatorColor = MoimTheme.colors.bg.input,
        errorIndicatorColor = MoimTheme.colors.bg.input,
        // text
        focusedTextColor = MoimTheme.colors.text.text01,
        unfocusedTextColor = MoimTheme.colors.text.text01,
        disabledTextColor = MoimTheme.colors.text.text01,
        errorTextColor = MoimTheme.colors.text.text01,
        // supportText
        focusedSupportingTextColor = MoimTheme.colors.text.text01,
        unfocusedSupportingTextColor = MoimTheme.colors.text.text01,
        disabledSupportingTextColor = MoimTheme.colors.text.text01,
        errorSupportingTextColor = MoimTheme.colors.global.red,
        // leftIcon
        focusedLeadingIconColor = MoimTheme.colors.icon,
        unfocusedLeadingIconColor = MoimTheme.colors.icon,
        disabledLeadingIconColor = MoimTheme.colors.gray.gray06,
        errorLeadingIconColor = MoimTheme.colors.icon,
        // rightIcon
        focusedTrailingIconColor = MoimTheme.colors.icon,
        unfocusedTrailingIconColor = MoimTheme.colors.icon,
        disabledTrailingIconColor = MoimTheme.colors.gray.gray06,
        errorTrailingIconColor = MoimTheme.colors.icon,
        // placeHolder
        focusedPlaceholderColor = MoimTheme.colors.gray.gray05,
        unfocusedPlaceholderColor = MoimTheme.colors.gray.gray05,
        disabledPlaceholderColor = MoimTheme.colors.gray.gray05,
        errorPlaceholderColor = MoimTheme.colors.gray.gray05,
        // cursor
        cursorColor = MoimTheme.colors.global.primary,
        errorCursorColor = MoimTheme.colors.global.primary,
    )

@Composable
fun moimButtomColors() =
    ButtonDefaults.buttonColors(
        containerColor = MoimTheme.colors.global.primary,
        contentColor = MoimTheme.colors.primary.text,
        disabledContainerColor = MoimTheme.colors.primary.disable,
        disabledContentColor = MoimTheme.colors.primary.disableText,
    )

@Composable
fun moimSwitchColors() =
    SwitchDefaults.colors(
        checkedThumbColor = MoimTheme.colors.primary.grayWhite,
        checkedBorderColor = MoimTheme.colors.global.primary,
        checkedTrackColor = MoimTheme.colors.global.primary,
        uncheckedThumbColor = MoimTheme.colors.primary.grayWhite,
        uncheckedBorderColor = MoimTheme.colors.gray.gray07,
        uncheckedTrackColor = MoimTheme.colors.gray.gray07,
    )
