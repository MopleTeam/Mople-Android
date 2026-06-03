package com.moim.feature.meetingnoticewrite

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.viewModelScope
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.data.datasource.notice.NoticeRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.NoticeAction
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.ToastMessage
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import com.moim.core.ui.view.restartableStateIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException

@HiltViewModel(assistedFactory = MeetingNoticeWriteViewModel.Factory::class)
class MeetingNoticeWriteViewModel @AssistedInject constructor(
    private val noticeRepository: NoticeRepository,
    private val noticeEventBus: EventBus<NoticeAction>,
    @Assisted val meetingNoticeWriteRoute: DetailRoute.MeetingNoticeWrite,
) : BaseViewModel() {
    private val meetId: String = meetingNoticeWriteRoute.meetId
    private val noticeId: String? = meetingNoticeWriteRoute.noticeId

    private val noticeWriteUiState =
        flow {
            if (noticeId == null) {
                emit(null)
            } else {
                emitAll(
                    noticeRepository.getNotice(
                        meetId = meetId,
                        noticeId = noticeId,
                    ),
                )
            }
        }.mapLatest { notice ->
            MeetingNoticeWriteUiState.Success(
                meetId = meetId,
                noticeId = notice?.noticeId,
                noticeState = TextFieldState(initialText = notice?.content.orEmpty()),
                enabled = !notice?.content.isNullOrEmpty(),
            )
        }.asResult()
            .mapLatest { result ->
                when (result) {
                    is Result.Loading -> MeetingNoticeWriteUiState.Loading
                    is Result.Success -> result.data
                    is Result.Error -> MeetingNoticeWriteUiState.Error
                }
            }.restartableStateIn(viewModelScope, SharingStarted.Lazily, MeetingNoticeWriteUiState.Loading)

    init {
        viewModelScope.launch {
            noticeWriteUiState.collect { uiState ->
                setUiState(uiState)
            }
        }
    }

    fun onUiAction(uiAction: MeetingNoticeWriteUiAction) {
        when (uiAction) {
            is MeetingNoticeWriteUiAction.OnClickBack -> {
                setUiEvent(MeetingNoticeWriteUiEvent.NavigateToBack)
            }

            is MeetingNoticeWriteUiAction.OnClickRefresh -> {
                noticeWriteUiState.restart()
            }

            is MeetingNoticeWriteUiAction.OnChangeEnable -> {
                uiState.checkState<MeetingNoticeWriteUiState.Success> {
                    setUiState(copy(enabled = uiAction.isEnable))
                }
            }

            is MeetingNoticeWriteUiAction.OnClickConfirm -> {
                saveNotice(
                    meetId = uiAction.meetId,
                    noticeId = uiAction.noticeId,
                )
            }
        }
    }

    private fun saveNotice(
        meetId: String,
        noticeId: String?,
    ) {
        viewModelScope.launch {
            uiState.checkState<MeetingNoticeWriteUiState.Success> {
                val content = noticeState.text.toString()
                if (content.isBlank()) return@checkState

                if (noticeId.isNullOrEmpty()) {
                    noticeRepository.createNotice(
                        meetId = meetId,
                        content = content,
                    )
                } else {
                    noticeRepository.updateNotice(
                        noticeId = noticeId,
                        meetId = meetId,
                        content = content,
                    )
                }.asResult()
                    .onEach { setLoading(it is Result.Loading) }
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> {
                                return@collect
                            }

                            is Result.Success -> {
                                if (noticeId.isNullOrEmpty()) {
                                    noticeEventBus.send(NoticeAction.NoticeCreate(notice = result.data))
                                } else {
                                    noticeEventBus.send(NoticeAction.NoticeUpdate(notice = result.data))
                                }
                                setUiEvent(MeetingNoticeWriteUiEvent.NavigateToBack)
                            }

                            is Result.Error -> {
                                val toastMessage =
                                    when (result.exception) {
                                        is IOException -> ToastMessage.NetworkErrorMessage
                                        else -> ToastMessage.ServerErrorMessage
                                    }
                                setUiEvent(MeetingNoticeWriteUiEvent.ShowToastMessage(toastMessage))
                            }
                        }
                    }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(meetingDetailRoute: DetailRoute.MeetingNoticeWrite): MeetingNoticeWriteViewModel
    }
}

sealed interface MeetingNoticeWriteUiState : UiState {
    data object Loading : MeetingNoticeWriteUiState

    data class Success(
        val meetId: String = "",
        val noticeId: String? = null,
        val noticeState: TextFieldState = TextFieldState(),
        val enabled: Boolean = false,
    ) : MeetingNoticeWriteUiState

    data object Error : MeetingNoticeWriteUiState
}

sealed interface MeetingNoticeWriteUiAction : UiAction {
    data object OnClickBack : MeetingNoticeWriteUiAction

    data object OnClickRefresh : MeetingNoticeWriteUiAction

    data class OnChangeEnable(
        val isEnable: Boolean,
    ) : MeetingNoticeWriteUiAction

    data class OnClickConfirm(
        val meetId: String,
        val noticeId: String?,
    ) : MeetingNoticeWriteUiAction
}

sealed interface MeetingNoticeWriteUiEvent : UiEvent {
    data object NavigateToBack : MeetingNoticeWriteUiEvent

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : MeetingNoticeWriteUiEvent
}
