package com.moim.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.moim.core.designsystem.R

private val PretendardFontFamily = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal)
)

private val PretendardStyle = TextStyle(
    fontFamily = PretendardFontFamily,
    fontWeight = FontWeight.W600,
)

internal val Typography = MoimTypography(
    bold48 = PretendardStyle.copy(
        fontWeight = FontWeight.W600,
        fontSize = 48.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.2).sp
    ),
    bold36 = PretendardStyle.copy(
        fontWeight = FontWeight.W600,
        fontSize = 36.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.2).sp
    ),
    bold32 = PretendardStyle.copy(
        fontWeight = FontWeight.W600,
        fontSize = 32.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.2).sp
    ),
    bold28 = PretendardStyle.copy(
        fontWeight = FontWeight.W600,
        fontSize = 28.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.2).sp
    ),
    bold24 = PretendardStyle.copy(
        fontWeight = FontWeight.W600,
        fontSize = 24.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.2).sp
    ),
    bold18 = PretendardStyle.copy(
        fontWeight = FontWeight.W600,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.2).sp
    ),
    bold16 = PretendardStyle.copy(
        fontWeight = FontWeight.W600,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.2).sp
    ),
    bold14 = PretendardStyle.copy(
        fontWeight = FontWeight.W600,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = (-0.2).sp
    ),
    bold12 = PretendardStyle.copy(
        fontWeight = FontWeight.W600,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = (-0.2).sp
    ),
    bold11 = PretendardStyle.copy(
        fontWeight = FontWeight.W600,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = (-0.2).sp
    ),
    regular48 = PretendardStyle.copy(
        fontWeight = FontWeight.W400,
        fontSize = 48.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.2).sp
    ),
    regular36 = PretendardStyle.copy(
        fontWeight = FontWeight.W400,
        fontSize = 36.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.2).sp
    ),
    regular32 = PretendardStyle.copy(
        fontWeight = FontWeight.W400,
        fontSize = 32.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.2).sp
    ),
    regular28 = PretendardStyle.copy(
        fontWeight = FontWeight.W400,
        fontSize = 28.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.2).sp
    ),
    regular24 = PretendardStyle.copy(
        fontWeight = FontWeight.W400,
        fontSize = 24.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.2).sp
    ),
    regular18 = PretendardStyle.copy(
        fontWeight = FontWeight.W400,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.2).sp
    ),
    regular16 = PretendardStyle.copy(
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.2).sp
    ),
    regular14 = PretendardStyle.copy(
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = (-0.2).sp
    ),
    regular12 = PretendardStyle.copy(
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = (-0.2).sp
    ),
    regular11 = PretendardStyle.copy(
        fontWeight = FontWeight.W400,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = (-0.2).sp
    )
)
val LocalTypography = staticCompositionLocalOf {
    Typography
}

@Immutable
data class MoimTypography(
    val bold48: TextStyle,
    val bold36: TextStyle,
    val bold32: TextStyle,
    val bold28: TextStyle,
    val bold24: TextStyle,
    val bold18: TextStyle,
    val bold16: TextStyle,
    val bold14: TextStyle,
    val bold12: TextStyle,
    val bold11: TextStyle,
    val regular48: TextStyle,
    val regular36: TextStyle,
    val regular32: TextStyle,
    val regular28: TextStyle,
    val regular24: TextStyle,
    val regular18: TextStyle,
    val regular16: TextStyle,
    val regular14: TextStyle,
    val regular12: TextStyle,
    val regular11: TextStyle
)

