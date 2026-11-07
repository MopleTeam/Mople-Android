package com.moim.feature.intro.screen.signup.model

import androidx.compose.runtime.Immutable

@Immutable
data class SignUpState(
    val email: String = "",
    val token: String = "",
    val profileUrl: String? = null,
    val nickname: String = "",
    val isDuplicatedName: Boolean? = null,
    val isRegexError: Boolean = false,
    val enableSignUp: Boolean = false,
    val isShowProfileEditDialog: Boolean = false,
)
