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
    Font(R.font.pretendard_bold, FontWeight.W700),
    Font(R.font.pretendard_semi_bold, FontWeight.W700),
    Font(R.font.pretendard_medium, FontWeight.W700),
    Font(R.font.pretendard_regular, FontWeight.Normal)
)

private val PretendardStyle = TextStyle(fontFamily = PretendardFontFamily)

val Typography = MoimTypography(
    heading = Heading(
        bold = PretendardStyle.copy(
            fontFamily = FontFamily(Font(R.font.pretendard_bold, FontWeight.W700)),
            fontWeight = FontWeight.W700,
            fontSize = 22.sp,
            lineHeight = 30.8.sp,
            letterSpacing = (-0.2).sp
        )
    ),
    title01 = Title01(
        bold = PretendardStyle.copy(
            fontFamily = FontFamily(Font(R.font.pretendard_bold, FontWeight.W700)),
            fontWeight = FontWeight.W700,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            letterSpacing = (-0.2).sp
        )
    ),
    title02 = Title02(
        bold = PretendardStyle.copy(
            fontFamily = FontFamily(Font(R.font.pretendard_bold, FontWeight.W700)),
            fontWeight = FontWeight.W700,
            fontSize = 18.sp,
            lineHeight = 25.2.sp,
            letterSpacing = (-0.2).sp
        ),
        semiBold = PretendardStyle.copy(
            fontFamily = FontFamily(Font(R.font.pretendard_semi_bold, FontWeight.W600)),
            fontWeight = FontWeight.W600,
            fontSize = 18.sp,
            lineHeight = 20.sp,
            letterSpacing = (-0.2).sp
        )
    ),
    title03 = Title03(
        bold = PretendardStyle.copy(
            fontFamily = FontFamily(Font(R.font.pretendard_bold, FontWeight.W700)),
            fontWeight = FontWeight.W700,
            fontSize = 16.sp,
            lineHeight = 22.4.sp,
            letterSpacing = (-0.2).sp
        ),
        semiBold = PretendardStyle.copy(
            fontFamily = FontFamily(Font(R.font.pretendard_semi_bold, FontWeight.W600)),
            fontWeight = FontWeight.W600,
            fontSize = 16.sp,
            lineHeight = 22.4.sp,
            letterSpacing = (-0.2).sp
        ),
        medium = PretendardStyle.copy(
            fontFamily = FontFamily(Font(R.font.pretendard_medium, FontWeight.W600)),
            fontWeight = FontWeight.W600,
            fontSize = 16.sp,
            lineHeight = 22.4.sp,
            letterSpacing = (-0.2).sp
        )
    ),
    body01 = Body01(
        semiBold = PretendardStyle.copy(
            fontFamily = FontFamily(Font(R.font.pretendard_semi_bold, FontWeight.W600)),
            fontWeight = FontWeight.W600,
            fontSize = 14.sp,
            lineHeight = 19.6.sp,
            letterSpacing = (-0.2).sp
        ),
        medium = PretendardStyle.copy(
            fontFamily = FontFamily(Font(R.font.pretendard_medium, FontWeight.W600)),
            fontWeight = FontWeight.W600,
            fontSize = 14.sp,
            lineHeight = 19.6.sp,
            letterSpacing = (-0.2).sp
        ),
        regular = PretendardStyle.copy(
            fontFamily = FontFamily(Font(R.font.pretendard_regular, FontWeight.W600)),
            fontWeight = FontWeight.W600,
            fontSize = 14.sp,
            lineHeight = 19.6.sp,
            letterSpacing = (-0.2).sp
        )
    ),
    body02 = Body02(
        bold = PretendardStyle.copy(
            fontFamily = FontFamily(Font(R.font.pretendard_bold, FontWeight.W700)),
            fontWeight = FontWeight.W700,
            fontSize = 12.sp,
            lineHeight = 16.8.sp,
            letterSpacing = (-0.2).sp
        ),
        semiBold = PretendardStyle.copy(
            fontFamily = FontFamily(Font(R.font.pretendard_semi_bold, FontWeight.W600)),
            fontWeight = FontWeight.W600,
            fontSize = 12.sp,
            lineHeight = 16.8.sp,
            letterSpacing = (-0.2).sp
        ),
        medium = PretendardStyle.copy(
            fontFamily = FontFamily(Font(R.font.pretendard_medium, FontWeight.W600)),
            fontWeight = FontWeight.W600,
            fontSize = 12.sp,
            lineHeight = 16.8.sp,
            letterSpacing = (-0.2).sp
        ),
        regular = PretendardStyle.copy(
            fontFamily = FontFamily(Font(R.font.pretendard_regular, FontWeight.W600)),
            fontWeight = FontWeight.W600,
            fontSize = 12.sp,
            lineHeight = 16.8.sp,
            letterSpacing = (-0.2).sp
        )
    ),
)

val LocalTypography = staticCompositionLocalOf {
    Typography
}

@Immutable
data class MoimTypography(
    val heading: Heading,
    val title01: Title01,
    val title02: Title02,
    val title03: Title03,
    val body01: Body01,
    val body02: Body02
)

@Immutable
data class Heading( // 22.px
    val bold: TextStyle
)

@Immutable
data class Title01( // 20.px
    val bold: TextStyle
)

@Immutable
data class Title02( // 18.px
    val bold: TextStyle,
    val semiBold: TextStyle
)

@Immutable
data class Title03( // 16.px
    val bold: TextStyle,
    val semiBold: TextStyle,
    val medium: TextStyle
)

@Immutable
data class Body01( // 14.px
    val semiBold: TextStyle,
    val medium: TextStyle,
    val regular: TextStyle
)

@Immutable
data class Body02( // 12.px
    val bold: TextStyle,
    val semiBold: TextStyle,
    val medium: TextStyle,
    val regular: TextStyle
)