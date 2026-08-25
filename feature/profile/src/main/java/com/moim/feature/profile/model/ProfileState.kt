package com.moim.feature.profile.model

import androidx.compose.runtime.Immutable
import com.moim.core.common.model.User
import com.moim.core.common.result.Result

@Immutable
data class ProfileState(
    val user: Result<User> = Result.Loading,
    val isShowUserLogoutDialog: Boolean = false,
    val isShowUserDeleteDialog: Boolean = false,
) {
    val isSuccess
        get() = user is Result.Success

    val isError
        get() = user is Result.Error

    val isLoading
        get() = user is Result.Loading
}
