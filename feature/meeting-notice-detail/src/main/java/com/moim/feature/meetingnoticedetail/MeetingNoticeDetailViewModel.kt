package com.moim.feature.meetingnoticedetail

import androidx.lifecycle.viewModelScope
import com.moim.core.data.datasource.notice.NoticeRepository
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel(assistedFactory = MeetingNoticeDetailViewModel.Factory::class)
class MeetingNoticeDetailViewModel @Inject constructor(
    private val noticeRepository: NoticeRepository,
    @Assisted val meetingNoticeDetailRoute: DetailRoute.MeetingNoticeDetail,
) : BaseViewModel() {
    init {
        viewModelScope.launch {
            setUiState(MeetingNoticeDetailUiState())
        }
    }

    fun onUiAction(uiAction: MeetingNoticeDetailUiAction) {
        when (uiAction) {
            is MeetingNoticeDetailUiAction.OnClickBack -> setUiEvent(MeetingNoticeDetailUiEvent.NavigateToBack)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(meetingDetailRoute: DetailRoute.MeetingNoticeDetail): MeetingNoticeDetailViewModel
    }
}

data class MeetingNoticeDetailUiState(
    val placeholder: Unit = Unit,
) : UiState

sealed interface MeetingNoticeDetailUiAction : UiAction {
    data object OnClickBack : MeetingNoticeDetailUiAction
}

sealed interface MeetingNoticeDetailUiEvent : UiEvent {
    data object NavigateToBack : MeetingNoticeDetailUiEvent
}
