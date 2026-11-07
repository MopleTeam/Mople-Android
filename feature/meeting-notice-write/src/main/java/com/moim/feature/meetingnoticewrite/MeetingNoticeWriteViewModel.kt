package com.moim.feature.meetingnoticewrite

import androidx.compose.foundation.text.input.TextFieldState
import com.moim.core.common.result.Result
import com.moim.core.data.datasource.notice.NoticeRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.NoticeAction
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.view.ToastMessage
import com.moim.feature.meetingnoticewrite.model.MeetingNoticeWriteIntent
import com.moim.feature.meetingnoticewrite.model.MeetingNoticeWriteSideEffect
import com.moim.feature.meetingnoticewrite.model.MeetingNoticeWriteState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import org.orbitmvi.orbit.syntax.Syntax
import java.io.IOException

@HiltViewModel(assistedFactory = MeetingNoticeWriteViewModel.Factory::class)
class MeetingNoticeWriteViewModel @AssistedInject constructor(
    private val noticeRepository: NoticeRepository,
    private val noticeEventBus: EventBus<NoticeAction>,
    @Assisted val meetingNoticeWriteRoute: DetailRoute.MeetingNoticeWrite,
) : MVIViewModel<MeetingNoticeWriteState, MeetingNoticeWriteSideEffect>(meetingNoticeWriteRoute.asState()) {
    private val noticeId: String? = meetingNoticeWriteRoute.noticeId

    override suspend fun Syntax<MeetingNoticeWriteState, MeetingNoticeWriteSideEffect>.onContainerCreate() {
        loadNotice()
    }

    override fun onIntent(intent: Intent) {
        if (intent !is MeetingNoticeWriteIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is MeetingNoticeWriteIntent.BackClick -> {
                    postSideEffect(MeetingNoticeWriteSideEffect.NavigateToBack)
                }

                is MeetingNoticeWriteIntent.RefreshClick -> {
                    loadNotice()
                }

                is MeetingNoticeWriteIntent.EnableChange -> {
                    reduce { state.copy(enabled = intent.isEnable) }
                }

                is MeetingNoticeWriteIntent.ConfirmClick -> {
                    saveNotice(
                        meetId = intent.meetId,
                        noticeId = intent.noticeId,
                    )
                }
            }
        }
    }

    private fun loadNotice() {
        intent {
            reduce { state.copy(loadState = Result.Loading) }

            try {
                val notice = noticeId?.let { noticeRepository.getNotice(noticeId = it) }
                val content = notice?.content.orEmpty()

                reduce {
                    state.copy(
                        loadState = Result.Success(Unit),
                        noticeId = notice?.noticeId,
                        noticeState = TextFieldState(initialText = content),
                        enabled = content.isNotEmpty(),
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                reduce { state.copy(loadState = Result.Error(e)) }
            }
        }
    }

    private suspend fun Syntax<MeetingNoticeWriteState, MeetingNoticeWriteSideEffect>.saveNotice(
        meetId: String,
        noticeId: String?,
    ) {
        val content = state.noticeState.text.toString()
        if (content.isBlank()) return

        setLoading(true)

        try {
            val notice =
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
                }

            if (noticeId.isNullOrEmpty()) {
                noticeEventBus.send(NoticeAction.NoticeCreate(notice = notice))
            } else {
                noticeEventBus.send(NoticeAction.NoticeUpdate(notice = notice))
            }

            postSideEffect(MeetingNoticeWriteSideEffect.NavigateToBack)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<MeetingNoticeWriteState, MeetingNoticeWriteSideEffect>.showErrorToast(exception: Throwable) {
        val message = if (exception is IOException) ToastMessage.NetworkErrorMessage else ToastMessage.ServerErrorMessage
        postSideEffect(MeetingNoticeWriteSideEffect.ShowToastMessage(message))
    }

    @AssistedFactory
    interface Factory {
        fun create(meetingDetailRoute: DetailRoute.MeetingNoticeWrite): MeetingNoticeWriteViewModel
    }
}

private fun DetailRoute.MeetingNoticeWrite.asState() =
    MeetingNoticeWriteState(
        meetId = meetId,
        noticeId = noticeId,
    )
