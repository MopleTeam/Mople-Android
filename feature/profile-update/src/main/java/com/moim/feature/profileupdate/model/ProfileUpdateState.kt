package com.moim.feature.profileupdate.model

import androidx.compose.runtime.Immutable
import com.moim.core.common.model.User
import com.moim.core.common.result.Result
import com.moim.core.common.result.data

@Immutable
data class ProfileUpdateState(
    val user: Result<User> = Result.Loading,
    val profileUrl: String? = null,
    val nickname: String = "",
    val isDuplicatedName: Boolean? = null,
    val isRegexError: Boolean = false,
    val isShowProfileEditDialog: Boolean = false,
    val enableProfileUpdate: Boolean = false,
) {
    // 중복 검사 스킵 판단용 원본 닉네임
    val currentNickname
        get() = user.data?.nickname.orEmpty()

    val isSuccess
        get() = user is Result.Success

    val isError
        get() = user is Result.Error

    val isLoading
        get() = user is Result.Loading
}
