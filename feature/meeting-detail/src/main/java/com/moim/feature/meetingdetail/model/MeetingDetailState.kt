package com.moim.feature.meetingdetail.model

import androidx.compose.runtime.Immutable
import com.moim.core.common.model.Meeting
import com.moim.core.common.model.item.PlanItem
import com.moim.core.common.result.Result
import com.moim.core.ui.view.PagingUiState

@Immutable
data class MeetingDetailState(
    val userId: String = "",
    val meeting: Result<Meeting> = Result.Loading,
    val notice: MeetingDetailNoticeUiModel? = null,
    val isShowApplyCancelDialog: Boolean = false,
    val isPlanSelected: Boolean = true,
    val cancelPlanItem: PlanItem? = null,
    val plans: List<PlanItem> = emptyList(),
    val reviews: List<PlanItem> = emptyList(),
    val plansPagingInfo: PagingUiState = PagingUiState(),
    val reviewsPagingInfo: PagingUiState = PagingUiState(),
    val planTotalCount: Int = 0,
    val reviewTotalCount: Int = 0,
) {
    val isSuccess
        get() = meeting is Result.Success

    val isError
        get() = meeting is Result.Error

    val isLoading
        get() = meeting is Result.Loading
}
