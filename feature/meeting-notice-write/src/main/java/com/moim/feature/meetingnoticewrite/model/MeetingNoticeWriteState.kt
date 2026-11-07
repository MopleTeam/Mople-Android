package com.moim.feature.meetingnoticewrite.model

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import com.moim.core.common.result.Result

@Stable
data class MeetingNoticeWriteState(
    val meetId: String = "",
    val noticeId: String? = null,
    // 불러온 공지는 noticeState에 담기므로 로드 상태만 들고 있는다.
    val loadState: Result<Unit> = Result.Loading,
    val noticeState: TextFieldState = TextFieldState(),
    val enabled: Boolean = false,
) {
    val isSuccess
        get() = loadState is Result.Success

    val isError
        get() = loadState is Result.Error

    val isLoading
        get() = loadState is Result.Loading
}
