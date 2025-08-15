package com.moim.feature.participantlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.ToastMessage
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.domain.usecase.GetParticipantsUseCase
import com.moim.core.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ParticipantListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val meetingRepository: MeetingRepository,
    getParticipantsUseCase: GetParticipantsUseCase,
) : BaseViewModel() {

    private val isMeeting
        get() = savedStateHandle.get<Boolean>(KEY_IS_MEETING) ?: false

    private val isPlan
        get() = savedStateHandle.get<Boolean>(KEY_IS_PLAN) ?: false

    private val id
        get() = savedStateHandle.get<String>(KEY_ID) ?: ""

    private val participants = getParticipantsUseCase(
        params = GetParticipantsUseCase.Params(
            id = id,
            isMeeting = isMeeting,
            isPlan = isPlan
        )
    ).cachedIn(viewModelScope)

    init {
        setUiState(
            ParticipantListUiState(
                isMeeting = isMeeting,
                participant = participants
            )
        )
    }

    fun onUiAction(uiAction: ParticipantListUiAction) {
        when (uiAction) {
            is ParticipantListUiAction.OnClickBack -> setUiEvent(ParticipantListUiEvent.NavigateToBack)
            is ParticipantListUiAction.OnClickUserImage -> setUiEvent(ParticipantListUiEvent.NavigateToImageViewer(uiAction.userImage, uiAction.userName))
            is ParticipantListUiAction.OnClickMeetingInvite -> getInviteLink()
        }
    }

    private fun getInviteLink() {
        viewModelScope.launch {
            meetingRepository
                .getMeetingInviteCode(id)
                .asResult()
                .onEach { setLoading(it is Result.Loading) }
                .collect { result ->
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> setUiEvent(ParticipantListUiEvent.NavigateToExternalShareUrl(result.data))
                        is Result.Error -> when (result.exception) {
                            is IOException -> setUiEvent(ParticipantListUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
                            is NetworkException -> setUiEvent(ParticipantListUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
                        }
                    }
                }
        }
    }

    companion object {
        private const val KEY_IS_MEETING = "isMeeting"
        private const val KEY_IS_PLAN = "isPlan"
        private const val KEY_ID = "id"
    }
}

data class ParticipantListUiState(
    val isMeeting: Boolean,
    val participant: Flow<PagingData<User>>? = null,
) : UiState

sealed interface ParticipantListUiAction : UiAction {
    data object OnClickBack : ParticipantListUiAction

    data object OnClickMeetingInvite : ParticipantListUiAction

    data class OnClickUserImage(
        val userImage: String,
        val userName: String
    ) : ParticipantListUiAction
}

sealed interface ParticipantListUiEvent : UiEvent {
    data object NavigateToBack : ParticipantListUiEvent

    data class NavigateToImageViewer(
        val userImage: String,
        val userName: String
    ) : ParticipantListUiEvent

    data class NavigateToExternalShareUrl(
        val url: String
    ) : ParticipantListUiEvent

    data class ShowToastMessage(val toastMessage: ToastMessage) : ParticipantListUiEvent
}