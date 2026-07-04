package com.moim.feature.meetingdetail.model

import com.moim.core.common.model.Meeting
import com.moim.core.common.model.item.PlanItem
import com.moim.core.ui.view.PagingUiState
import com.moim.core.ui.view.UiState

sealed interface MeetingDetailUiState : UiState {
    data object Loading : MeetingDetailUiState

    data class Success(
        val userId: String = "",
        val meeting: Meeting = Meeting(),
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
    ) : MeetingDetailUiState

    data object Error : MeetingDetailUiState
}
