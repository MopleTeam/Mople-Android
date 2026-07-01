package com.moim.feature.meetingnoticedetail

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.viewModelScope
import com.moim.core.common.exception.ForbiddenException
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.exception.NotFoundException
import com.moim.core.common.model.Comment
import com.moim.core.common.model.Meeting
import com.moim.core.common.model.Notice
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.User
import com.moim.core.common.model.item.CommentUiModel
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.crashreport.CrashReporter
import com.moim.core.data.datasource.comment.CommentRepository
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.notice.NoticeRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.NoticeAction
import com.moim.core.ui.eventbus.actionStateIn
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.util.createCommentUiModel
import com.moim.core.ui.util.isActiveCheck
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.PagingHelper
import com.moim.core.ui.view.PagingUiState
import com.moim.core.ui.view.ToastMessage
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import com.moim.core.ui.view.restartableStateIn
import com.moim.feature.meetingnoticedetail.model.NoticeUiModel
import com.moim.feature.meetingnoticedetail.model.asUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException

@HiltViewModel(assistedFactory = MeetingNoticeDetailViewModel.Factory::class)
class MeetingNoticeDetailViewModel @AssistedInject constructor(
    userRepository: UserRepository,
    meetingRepository: MeetingRepository,
    private val noticeRepository: NoticeRepository,
    private val commentRepository: CommentRepository,
    private val crashReporter: CrashReporter,
    private val noticeEventBus: EventBus<NoticeAction>,
    @Assisted val meetingNoticeDetailRoute: DetailRoute.MeetingNoticeDetail,
) : BaseViewModel() {
    private val meetId = meetingNoticeDetailRoute.meetId
    private val noticeId = meetingNoticeDetailRoute.noticeId

    private var commentsPagingJob: Job? = null

    private val noticeActionReceiver =
        noticeEventBus
            .action
            .actionStateIn(viewModelScope, NoticeAction.None)

    private val noticeDetailUiState =
        combine(
            userRepository.getUser(),
            getNoticeFlow(),
            meetingRepository.getMeeting(meetId),
        ) { user, notice, meeting ->
            MeetingNoticeDetailUiState.Success(
                user = user,
                notice = notice.asUiModel(),
                isHostUser = meeting.hostId == user.userId,
            )
        }.asResult()
            .mapLatest { result ->
                when (result) {
                    is Result.Loading -> {
                        MeetingNoticeDetailUiState.Loading
                    }

                    is Result.Success -> {
                        result.data
                    }

                    is Result.Error -> {
                        when (result.exception) {
                            is ForbiddenException,
                            is NotFoundException,
                            -> {
                                MeetingNoticeDetailUiState.NotFoundError
                            }

                            else -> {
                                crashReporter.logException(result.exception)
                                MeetingNoticeDetailUiState.CommonError
                            }
                        }
                    }
                }
            }.restartableStateIn(viewModelScope, SharingStarted.Lazily, MeetingNoticeDetailUiState.Loading)

    init {
        viewModelScope.launch {
            launch {
                noticeDetailUiState.collect { uiState ->
                    if (uiState is MeetingNoticeDetailUiState.Success) {
                        val current = this@MeetingNoticeDetailViewModel.uiState.value as? MeetingNoticeDetailUiState.Success
                        setUiState(
                            uiState.copy(
                                comments = current?.comments ?: emptyList(),
                                commentsPagingInfo = current?.commentsPagingInfo ?: PagingUiState(),
                            ),
                        )
                    } else {
                        setUiState(uiState)
                    }
                }
            }

            launch {
                noticeDetailUiState
                    .filterIsInstance<MeetingNoticeDetailUiState.Success>()
                    .take(1)
                    .collect { getComments() }
            }

            launch {
                noticeActionReceiver.collect { action ->
                    when (action) {
                        is NoticeAction.NoticeUpdate -> {
                            applyNoticeUpdate(action.notice)
                        }

                        else -> {
                            return@collect
                        }
                    }
                }
            }
        }
    }

    fun onUiAction(uiAction: MeetingNoticeDetailUiAction) {
        when (uiAction) {
            is MeetingNoticeDetailUiAction.OnClickBack -> {
                setUiEvent(MeetingNoticeDetailUiEvent.NavigateToBack)
            }

            is MeetingNoticeDetailUiAction.OnClickRefresh -> {
                noticeDetailUiState.restart()
                getComments()
            }

            is MeetingNoticeDetailUiAction.OnLoadNextCommentsPage -> {
                val current = uiState.value as? MeetingNoticeDetailUiState.Success ?: return
                getComments(current.commentsPagingInfo.nextCursor)
            }

            is MeetingNoticeDetailUiAction.OnClickCommentUpload -> {
                uploadComment()
            }

            is MeetingNoticeDetailUiAction.OnClickCommentWebLink -> {
                setUiEvent(MeetingNoticeDetailUiEvent.NavigateToWebBrowser(uiAction.webLink))
            }

            is MeetingNoticeDetailUiAction.OnShowNoticeEditDialog -> {
                uiState.checkState<MeetingNoticeDetailUiState.Success> {
                    setUiState(copy(isShowNoticeEditDialog = uiAction.isShow))
                }
            }

            is MeetingNoticeDetailUiAction.OnClickNoticeUpdate -> {
                val noticeId = noticeId ?: return
                setUiEvent(MeetingNoticeDetailUiEvent.NavigateToMeetingNoticeWrite(meetId, noticeId))
            }

            is MeetingNoticeDetailUiAction.OnClickNoticeDelete -> {
                deleteNotice()
            }
        }
    }

    private fun getNoticeFlow() =
        if (noticeId == null) {
            throw NotFoundException(message = "noticeId is null", throwable = null)
        } else {
            noticeRepository.getNotice(noticeId = noticeId)
        }

    private fun getComments(cursor: String? = null) {
        if (commentsPagingJob.isActiveCheck()) return
        val noticeId = noticeId ?: return
        commentsPagingJob =
            viewModelScope.launch {
                handleCommentsPagingData(
                    pagingInfo = null,
                    isLoading = true,
                    cursor = cursor,
                )

                val pagingInfo =
                    runCatching {
                        commentRepository.getNoticeComments(
                            noticeId = noticeId,
                            cursor = cursor ?: "",
                            size = 30,
                        )
                    }.getOrNull()

                if (!isActive) return@launch

                handleCommentsPagingData(
                    pagingInfo = pagingInfo,
                    isLoading = false,
                    cursor = cursor,
                )
            }
    }

    private fun handleCommentsPagingData(
        pagingInfo: PaginationContainer<List<Comment>>?,
        isLoading: Boolean,
        cursor: String?,
    ) {
        uiState.checkState<MeetingNoticeDetailUiState.Success> {
            val result =
                PagingHelper.handlePagingResult(
                    pagingData = pagingInfo,
                    isLoading = isLoading,
                    currentPagingInfo = commentsPagingInfo,
                    currentItems = comments,
                    isInitialLoad = cursor == null,
                    transform = { items -> items.map { it.createCommentUiModel() } },
                )

            setUiState(
                copy(
                    commentsPagingInfo = result.pagingInfo,
                    comments = result.items,
                ),
            )
        }
    }

    private fun uploadComment() {
        val noticeId = noticeId ?: return
        viewModelScope.launch {
            uiState.checkState<MeetingNoticeDetailUiState.Success> {
                val content = commentState.text.toString().trim()
                if (content.isEmpty()) return@checkState

                commentRepository
                    .createNoticeComment(
                        noticeId = noticeId,
                        content = content,
                    ).asResult()
                    .onEach { setLoading(it is Result.Loading) }
                    .collect { result ->
                        uiState.checkState<MeetingNoticeDetailUiState.Success> {
                            when (result) {
                                is Result.Loading -> {
                                    return@collect
                                }

                                is Result.Success -> {
                                    val newComment = result.data.createCommentUiModel()
                                    commentState.clearText()
                                    setUiState(
                                        copy(
                                            comments = listOf(newComment) + comments,
                                            commentsPagingInfo =
                                                commentsPagingInfo.copy(
                                                    totalCount = commentsPagingInfo.totalCount + 1,
                                                ),
                                        ),
                                    )
                                }

                                is Result.Error -> {
                                    showErrorToast(result.exception)
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun deleteNotice() {
        val noticeId = noticeId ?: return
        viewModelScope.launch {
            noticeRepository
                .deleteNotice(noticeId = noticeId)
                .asResult()
                .onEach { setLoading(it is Result.Loading) }
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            return@collect
                        }

                        is Result.Success -> {
                            noticeEventBus.send(NoticeAction.NoticeDelete(noticeId = noticeId))
                            setUiEvent(MeetingNoticeDetailUiEvent.NavigateToBack)
                        }

                        is Result.Error -> {
                            showErrorToast(result.exception)
                        }
                    }
                }
        }
    }

    private fun applyNoticeUpdate(notice: Notice) {
        if (notice.noticeId != noticeId) return
        uiState.checkState<MeetingNoticeDetailUiState.Success> {
            setUiState(copy(notice = notice.asUiModel()))
        }
    }

    private fun showErrorToast(exception: Throwable) {
        when (exception) {
            is IOException -> setUiEvent(MeetingNoticeDetailUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
            is NetworkException -> setUiEvent(MeetingNoticeDetailUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(meetingDetailRoute: DetailRoute.MeetingNoticeDetail): MeetingNoticeDetailViewModel
    }
}

sealed interface MeetingNoticeDetailUiState : UiState {
    data object Loading : MeetingNoticeDetailUiState

    data class Success(
        val user: User,
        val notice: NoticeUiModel,
        val isHostUser: Boolean = false,
        val isShowNoticeEditDialog: Boolean = false,
        val commentState: TextFieldState = TextFieldState(),
        val comments: List<CommentUiModel> = emptyList(),
        val commentsPagingInfo: PagingUiState = PagingUiState(),
    ) : MeetingNoticeDetailUiState

    data object NotFoundError : MeetingNoticeDetailUiState

    data object CommonError : MeetingNoticeDetailUiState
}

sealed interface MeetingNoticeDetailUiAction : UiAction {
    data object OnClickBack : MeetingNoticeDetailUiAction

    data object OnClickRefresh : MeetingNoticeDetailUiAction

    data object OnLoadNextCommentsPage : MeetingNoticeDetailUiAction

    data object OnClickCommentUpload : MeetingNoticeDetailUiAction

    data class OnClickCommentWebLink(
        val webLink: String,
    ) : MeetingNoticeDetailUiAction

    data class OnShowNoticeEditDialog(
        val isShow: Boolean,
    ) : MeetingNoticeDetailUiAction

    data object OnClickNoticeUpdate : MeetingNoticeDetailUiAction

    data object OnClickNoticeDelete : MeetingNoticeDetailUiAction
}

sealed interface MeetingNoticeDetailUiEvent : UiEvent {
    data object NavigateToBack : MeetingNoticeDetailUiEvent

    data class NavigateToMeetingNoticeWrite(
        val meetId: String,
        val noticeId: String,
    ) : MeetingNoticeDetailUiEvent

    data class NavigateToWebBrowser(
        val webLink: String,
    ) : MeetingNoticeDetailUiEvent

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : MeetingNoticeDetailUiEvent
}
