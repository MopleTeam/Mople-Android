package com.moim.feature.meetingwrite

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.MeetingAction
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.ToastMessage
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MeetingWriteViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val meetingRepository: MeetingRepository,
    private val meetingEventBus: EventBus<MeetingAction>,
) : BaseViewModel() {

    private val meeting
        get() = savedStateHandle
            .toRoute<DetailRoute.MeetingWrite>(DetailRoute.MeetingWrite.typeMap)
            .meeting

    init {
        viewModelScope.launch {
            meeting?.let {
                setUiState(
                    MeetingWriteUiState.MeetingWrite(
                        meetingId = it.id,
                        meetingUrl = it.imageUrl,
                        meetingName = it.name,
                        enableMeetingWrite = true
                    )
                )
            } ?: run { setUiState(MeetingWriteUiState.MeetingWrite()) }
        }
    }

    fun onUiAction(uiAction: MeetingWriteUiAction) {
        when (uiAction) {
            is MeetingWriteUiAction.OnClickMeetingWrite -> setMeeting()
            is MeetingWriteUiAction.OnClickBack -> setUiEvent(MeetingWriteUiEvent.NavigateToBack)
            is MeetingWriteUiAction.OnChangeMeetingPhotoUrl -> setMeetingPhotoUrl(uiAction.meetingPhotoUrl)
            is MeetingWriteUiAction.OnChangeMeetingName -> setMeetingName(uiAction.name)
            is MeetingWriteUiAction.OnShowMeetingPhotoEditDialog -> showMeetingPhotoEditDialog(uiAction.isShow)
            is MeetingWriteUiAction.OnNavigatePhotoPicker -> setUiEvent(MeetingWriteUiEvent.NavigateToPhotoPicker)
        }
    }

    private fun showMeetingPhotoEditDialog(isShow: Boolean) {
        uiState.checkState<MeetingWriteUiState.MeetingWrite> {
            setUiState(copy(isShowPhotoEditDialog = isShow))
        }
    }

    private fun setMeetingPhotoUrl(imageUrl: String?) {
        uiState.checkState<MeetingWriteUiState.MeetingWrite> {
            setUiState(copy(meetingUrl = imageUrl))
        }
    }

    private fun setMeetingName(name: String) {
        uiState.checkState<MeetingWriteUiState.MeetingWrite> {
            val trimName = name.trim()
            val enableMeetingWrite = trimName.length >= 2
            setUiState(copy(meetingName = trimName, enableMeetingWrite = enableMeetingWrite))
        }
    }

    private fun setMeeting() {
        viewModelScope.launch {
            uiState.checkState<MeetingWriteUiState.MeetingWrite> {
                if (meetingId.isNullOrEmpty()) {
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
                        is Result.Success -> {
                            if (meetingId.isNullOrEmpty()) {
                                meetingEventBus.send(MeetingAction.MeetingCreate(meeting = result.data))
                            } else {
                                meetingEventBus.send(MeetingAction.MeetingUpdate(meeting = result.data))
                            }
                            setUiEvent(MeetingWriteUiEvent.NavigateToBack)
                        }

                        is Result.Error -> when (result.exception) {
                            is IOException -> setUiEvent(MeetingWriteUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
                            else -> setUiEvent(MeetingWriteUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
                        }
                    }
                }
            }
        }
    }
}

sealed interface MeetingWriteUiState : UiState {
    data class MeetingWrite(
        val meetingId: String? = null,
        val meetingUrl: String? = null,
        val meetingName: String = "",
        val enableMeetingWrite: Boolean = false,
        val isShowPhotoEditDialog: Boolean = false
    ) : MeetingWriteUiState
}

sealed interface MeetingWriteUiAction : UiAction {
    data object OnClickMeetingWrite : MeetingWriteUiAction

    data object OnClickBack : MeetingWriteUiAction

    data class OnChangeMeetingPhotoUrl(
        val meetingPhotoUrl: String?
    ) : MeetingWriteUiAction

    data class OnChangeMeetingName(
        val name: String
    ) : MeetingWriteUiAction

    data class OnShowMeetingPhotoEditDialog(
        val isShow: Boolean
    ) : MeetingWriteUiAction

    data object OnNavigatePhotoPicker : MeetingWriteUiAction
}

sealed interface MeetingWriteUiEvent : UiEvent {
    data object NavigateToBack : MeetingWriteUiEvent

    data object NavigateToPhotoPicker : MeetingWriteUiEvent

    data class ShowToastMessage(val message: ToastMessage) : MeetingWriteUiEvent
}