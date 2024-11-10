package com.moim.feature.meetingwrite

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.common.view.checkState
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.designsystem.R
import com.moim.core.model.asItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeetingWriteViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val meetingRepository: MeetingRepository
) : BaseViewModel() {

    private val meetingId
        get() = savedStateHandle.get<String?>(KEY_MEETING_ID)

    private val meetingResult = combine(loadDataSignal, flowOf(meetingId), ::Pair)
        .filter { (_, id) -> id != null }
        .flatMapLatest { (_, meetingId) -> meetingRepository.getMeeting(meetingId!!).asResult() }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            if (meetingId == null) {
                setUiState(MeetingWriteUiState.Success())
            } else {
                meetingResult.collect { result ->
                    when (result) {
                        is Result.Loading -> setUiState(MeetingWriteUiState.Loading)
                        is Result.Success -> {
                            val meeting = result.data.asItem()

                            setUiState(
                                MeetingWriteUiState.Success(
                                    meetingId = meeting.id,
                                    meetingUrl = meeting.imageUrl,
                                    meetingName = meeting.name,
                                    enableMeetingWrite = true
                                )
                            )
                        }

                        is Result.Error -> setUiState(MeetingWriteUiState.Error)
                    }
                }
            }
        }
    }

    fun onUiAction(uiAction: MeetingWriteUiAction) {
        when (uiAction) {
            is MeetingWriteUiAction.OnClickMeetingWrite -> setMeeting()
            is MeetingWriteUiAction.OnClickBack -> setUiEvent(MeetingWriteUiEvent.NavigateToBack)
            is MeetingWriteUiAction.OnClickRefresh -> onRefresh()
            is MeetingWriteUiAction.OnChangeMeetingPhotoUrl -> setMeetingPhotoUrl(uiAction.meetingPhotoUrl)
            is MeetingWriteUiAction.OnChangeMeetingName -> setMeetingName(uiAction.name)
            is MeetingWriteUiAction.OnShowMeetingPhotoEditDialog -> showMeetingPhotoEditDialog(uiAction.isShow)
            is MeetingWriteUiAction.OnNavigatePhotoPicker -> setUiEvent(MeetingWriteUiEvent.NavigateToPhotoPicker)
        }
    }

    private fun showMeetingPhotoEditDialog(isShow: Boolean) {
        uiState.checkState<MeetingWriteUiState.Success> {
            setUiState(copy(isShowPhotoEditDialog = isShow))
        }
    }

    private fun setMeetingPhotoUrl(imageUrl: String?) {
        uiState.checkState<MeetingWriteUiState.Success> {
            setUiState(copy(meetingUrl = imageUrl))
        }
    }

    private fun setMeetingName(name: String) {
        uiState.checkState<MeetingWriteUiState.Success> {
            val trimName = name.trim()
            val enableMeetingWrite = trimName.length >= 2
            setUiState(copy(meetingName = trimName, enableMeetingWrite = enableMeetingWrite))
        }
    }

    private fun setMeeting() {
        viewModelScope.launch {
            uiState.checkState<MeetingWriteUiState.Success> {
                if (meetingId == null) {
                    meetingRepository.createMeeting(
                        meetingName = meetingName,
                        meetingImageUrl = meetingUrl
                    )
                } else {
                    meetingRepository.updateMeeting(
                        meetingId = meetingId,
                        meetingName = meetingName,
                        meetingImageUrl = meetingUrl
                    )
                }.asResult().onEach { setLoading(it is Result.Loading) }.collect { result ->
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> setUiEvent(MeetingWriteUiEvent.NavigateToBack)
                        is Result.Error -> setUiEvent(MeetingWriteUiEvent.ShowToastMessage(R.string.common_error_disconnection))
                    }
                }
            }
        }
    }

    companion object {
        private const val KEY_MEETING_ID = "meetingId"
    }
}

sealed interface MeetingWriteUiState : UiState {
    data object Loading : MeetingWriteUiState

    data class Success(
        val meetingId: String? = null,
        val meetingUrl: String? = null,
        val meetingName: String = "",
        val enableMeetingWrite: Boolean = false,
        val isShowPhotoEditDialog: Boolean = false
    ) : MeetingWriteUiState

    data object Error : MeetingWriteUiState
}

sealed interface MeetingWriteUiAction : UiAction {
    data object OnClickMeetingWrite : MeetingWriteUiAction
    data object OnClickBack : MeetingWriteUiAction
    data object OnClickRefresh : MeetingWriteUiAction
    data class OnChangeMeetingPhotoUrl(val meetingPhotoUrl: String?) : MeetingWriteUiAction
    data class OnChangeMeetingName(val name: String) : MeetingWriteUiAction
    data class OnShowMeetingPhotoEditDialog(val isShow: Boolean) : MeetingWriteUiAction
    data object OnNavigatePhotoPicker : MeetingWriteUiAction
}

sealed interface MeetingWriteUiEvent : UiEvent {
    data object NavigateToBack : MeetingWriteUiEvent
    data object NavigateToPhotoPicker : MeetingWriteUiEvent
    data class ShowToastMessage(@StringRes val messageRes: Int) : MeetingWriteUiEvent
}