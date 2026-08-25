package com.moim.feature.meetingwrite

import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.MeetingAction
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.view.ToastMessage
import com.moim.feature.meetingwrite.model.MeetingWriteIntent
import com.moim.feature.meetingwrite.model.MeetingWriteSideEffect
import com.moim.feature.meetingwrite.model.MeetingWriteState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import org.orbitmvi.orbit.syntax.Syntax
import java.io.IOException

@HiltViewModel(assistedFactory = MeetingWriteViewModel.Factory::class)
class MeetingWriteViewModel @AssistedInject constructor(
    private val meetingRepository: MeetingRepository,
    private val meetingEventBus: EventBus<MeetingAction>,
    @Assisted val meetingWriteRoute: DetailRoute.MeetingWrite,
) : MVIViewModel<MeetingWriteState, MeetingWriteSideEffect>(meetingWriteRoute.asState()) {
    override fun onIntent(intent: Intent) {
        if (intent !is MeetingWriteIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is MeetingWriteIntent.BackClick -> {
                    postSideEffect(MeetingWriteSideEffect.NavigateToBack)
                }

                is MeetingWriteIntent.MeetingWriteClick -> {
                    setMeeting()
                }

                is MeetingWriteIntent.PhotoPickerClick -> {
                    postSideEffect(MeetingWriteSideEffect.NavigateToPhotoPicker)
                }

                is MeetingWriteIntent.MeetingPhotoUrlChange -> {
                    reduce { state.copy(meetingUrl = intent.meetingPhotoUrl) }
                }

                is MeetingWriteIntent.MeetingNameChange -> {
                    val trimName = intent.name.trim()

                    reduce {
                        state.copy(
                            meetingName = trimName,
                            enableMeetingWrite = trimName.length >= 2,
                        )
                    }
                }

                is MeetingWriteIntent.MeetingPhotoEditDialogShow -> {
                    reduce { state.copy(isShowPhotoEditDialog = intent.isShow) }
                }
            }
        }
    }

    private suspend fun Syntax<MeetingWriteState, MeetingWriteSideEffect>.setMeeting() {
        val meetingId = state.meetingId
        val meetingName = state.meetingName
        val meetingImageUrl = state.meetingUrl

        setLoading(true)

        try {
            val meeting =
                if (meetingId.isNullOrEmpty()) {
                    meetingRepository.createMeeting(
                        meetingName = meetingName,
                        meetingImageUrl = meetingImageUrl,
                    )
                } else {
                    meetingRepository.updateMeeting(
                        meetingId = meetingId,
                        meetingName = meetingName,
                        meetingImageUrl = meetingImageUrl,
                    )
                }

            if (meetingId.isNullOrEmpty()) {
                meetingEventBus.send(MeetingAction.MeetingCreate(meeting = meeting))
            } else {
                meetingEventBus.send(MeetingAction.MeetingUpdate(meeting = meeting))
            }

            postSideEffect(MeetingWriteSideEffect.NavigateToBack)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<MeetingWriteState, MeetingWriteSideEffect>.showErrorToast(exception: Throwable) {
        val message = if (exception is IOException) ToastMessage.NetworkErrorMessage else ToastMessage.ServerErrorMessage
        postSideEffect(MeetingWriteSideEffect.ShowToastMessage(message))
    }

    @AssistedFactory
    interface Factory {
        fun create(meetingWriteRoute: DetailRoute.MeetingWrite): MeetingWriteViewModel
    }
}

private fun DetailRoute.MeetingWrite.asState() =
    meeting?.let {
        MeetingWriteState(
            meetingId = it.id,
            meetingUrl = it.imageUrl,
            meetingName = it.name,
            enableMeetingWrite = true,
        )
    } ?: MeetingWriteState()
