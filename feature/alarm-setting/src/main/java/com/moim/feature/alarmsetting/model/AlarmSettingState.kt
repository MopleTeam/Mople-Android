package com.moim.feature.alarmsetting.model

import androidx.compose.runtime.Immutable
import com.moim.core.common.result.Result

@Immutable
data class AlarmSettingState(
    val notifySetting: Result<NotifySetting> = Result.Loading,
) {
    val isSuccess
        get() = notifySetting is Result.Success

    val isError
        get() = notifySetting is Result.Error

    val isLoading
        get() = !isSuccess && !isError
}
