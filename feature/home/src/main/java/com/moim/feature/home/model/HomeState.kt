package com.moim.feature.home.model

import androidx.compose.runtime.Immutable
import com.moim.core.common.model.Plan
import com.moim.core.common.model.User
import com.moim.core.common.result.Result

@Immutable
data class HomeState(
    val user: User = User(""),
    val plans: Result<List<Plan>> = Result.Loading,
    val hasJoinedMeet: Boolean = false,
    val isPermissionCheck: Boolean = false,
) {
    val isSuccess
        get() = plans is Result.Success

    val isError
        get() = plans is Result.Error

    val isLoading
        get() = plans is Result.Loading
}
