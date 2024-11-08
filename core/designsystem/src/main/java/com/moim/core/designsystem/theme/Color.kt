package com.moim.core.designsystem.theme

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val color_000000 = Color(0xFF000000)
val color_222222 = Color(0xFF222222)
val color_333333 = Color(0xFF333333)
val color_3E3F40 = Color(0xff3E3F40)
val color_3E4145 = Color(0xff3E4145)
val color_555555 = Color(0XFF555555)
val color_7A7A7A = Color(0xFF7A7A7A)
val color_999999 = Color(0xFF999999)
val color_888888 = Color(0xFF888888)

val color_FFFFFF = Color(0xFFFFFFFF)
val color_F1F2F3 = Color(0xFFF1F2F3)
val color_E1E3E5 = Color(0xFFE1E3E5)
val color_CCCCCC = Color(0xFFCCCCCC)
val color_F5F5F5 = Color(0xFFF5F5F5)
val color_F6F8FA = Color(0xFFF6F8FA)
val color_F7F7F8 = Color(0xFFF7F7F8)
val color_F0F0F0 = Color(0xFFF0F0F0)
val color_F8F8F8 = Color(0xFFF8F8F8)
val color_F2F2F2 = Color(0XFFF2F2F2)
val color_D9D9D9 = Color(0XFFD9D9D9)

val color_983B3B = Color(0XFF983B3B)
val color_FF3B30 = Color(0XFFFF3B30)
val color_DC5D5D = Color(0XFFDC5D5D)
val color_34C759 = Color(0XFF34C759)
val color_FEE500 = Color(0xFFFEE500)
val color_3366FF = Color(0xFF3366FF)
val color_D6E0FF = Color(0xFFD6E0FF)


@Composable
fun moimTextFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = color_F6F8FA,
    unfocusedContainerColor = color_F6F8FA,
    errorContainerColor = color_F6F8FA,

    focusedIndicatorColor = color_F6F8FA,
    unfocusedIndicatorColor = color_F6F8FA,
    errorIndicatorColor = color_F6F8FA,

    focusedTextColor = color_222222,
    unfocusedTextColor = color_222222,
    errorTextColor = color_222222,

    focusedSupportingTextColor = color_222222,
    unfocusedSupportingTextColor = color_222222,
    errorSupportingTextColor = color_DC5D5D,

    focusedLeadingIconColor = color_222222,
    unfocusedLeadingIconColor = color_222222,
    errorLeadingIconColor = color_222222,

    focusedTrailingIconColor = color_222222,
    unfocusedTrailingIconColor = color_222222,
    errorTrailingIconColor = color_222222,

    focusedPlaceholderColor = color_999999,
    unfocusedPlaceholderColor = color_999999,
    errorPlaceholderColor = color_999999,

    cursorColor = color_3366FF,
    errorCursorColor = color_3366FF,
)

@Composable
fun moimButtomColors() = ButtonDefaults.buttonColors(
    containerColor = color_3366FF,
    contentColor = color_FFFFFF,
    disabledContainerColor = color_D6E0FF,
    disabledContentColor = color_FFFFFF
)