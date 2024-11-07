package com.moim.feature.meeting

import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiState
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.model.MeetingPlan
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MeetingViewModel @Inject constructor(
    private val meetingRepository: MeetingRepository
) : BaseViewModel() {

}

sealed interface MeetingUiState : UiState {
    data object Loading : MeetingUiState

    data class Success(
        val meetings: List<MeetingPlan>
    ) : MeetingUiState

    data object Error : MeetingUiState
}