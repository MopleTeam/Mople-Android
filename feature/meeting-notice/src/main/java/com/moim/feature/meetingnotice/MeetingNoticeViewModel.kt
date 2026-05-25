package com.moim.feature.meetingnotice

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.moim.core.data.datasource.notice.NoticeRepository
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = MeetingNoticeViewModel.Factory::class)
class MeetingNoticeViewModel @AssistedInject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val noticeRepository: NoticeRepository,
    @Assisted val meetingNoticeRoute: DetailRoute.MeetingNotice,
) : BaseViewModel() {
    private val meetingId = meetingNoticeRoute.meetId

    init {
        viewModelScope.launch {
        }
    }

    fun onUiAction(uiAction: MeetingNoticeUiAction) {
        when (uiAction) {
            is MeetingNoticeUiAction.OnClickBack -> {
                setUiEvent(MeetingNoticeUiEvent.NavigateToBack)
            }

            is MeetingNoticeUiAction.OnClickRefresh -> {
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(meetingDetailRoute: DetailRoute.MeetingNotice): MeetingNoticeViewModel
    }
}

sealed interface MeetingNoticeUiState : UiState {
    data object Loading : MeetingNoticeUiState

    data class Success(
        val any: Any,
    ) : MeetingNoticeUiState

    data object Error : MeetingNoticeUiState
}

sealed interface MeetingNoticeUiAction : UiAction {
    data object OnClickBack : MeetingNoticeUiAction

    data object OnClickRefresh : MeetingNoticeUiAction
}

sealed interface MeetingNoticeUiEvent : UiEvent {
    data object NavigateToBack : MeetingNoticeUiEvent
}
