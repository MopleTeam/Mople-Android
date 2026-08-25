package com.moim.feature.meetingnoticedetail

import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.insert
import androidx.lifecycle.viewModelScope
import com.moim.core.common.exception.ForbiddenException
import com.moim.core.common.exception.NotFoundException
import com.moim.core.common.model.Notice
import com.moim.core.common.model.NoticeComment
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.result.Result
import com.moim.core.crashreport.CrashReporter
import com.moim.core.data.datasource.comment.CommentRepository
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.notice.NoticeRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.NoticeAction
import com.moim.core.ui.eventbus.actionStateIn
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.util.createNoticeCommentUiModel
import com.moim.core.ui.util.isActiveCheck
import com.moim.core.ui.view.PagingHelper
import com.moim.core.ui.view.ToastMessage
import com.moim.feature.meetingnoticedetail.model.MeetingNoticeDetailIntent
import com.moim.feature.meetingnoticedetail.model.MeetingNoticeDetailSideEffect
import com.moim.feature.meetingnoticedetail.model.MeetingNoticeDetailState
import com.moim.feature.meetingnoticedetail.model.asUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import org.orbitmvi.orbit.syntax.Syntax
import java.io.IOException

@HiltViewModel(assistedFactory = MeetingNoticeDetailViewModel.Factory::class)
class MeetingNoticeDetailViewModel @AssistedInject constructor(
    private val userRepository: UserRepository,
    private val meetingRepository: MeetingRepository,
    private val noticeRepository: NoticeRepository,
    private val commentRepository: CommentRepository,
    private val crashReporter: CrashReporter,
    private val noticeEventBus: EventBus<NoticeAction>,
    @Assisted val meetingNoticeDetailRoute: DetailRoute.MeetingNoticeDetail,
) : MVIViewModel<MeetingNoticeDetailState, MeetingNoticeDetailSideEffect>(MeetingNoticeDetailState()) {
    private val meetId = meetingNoticeDetailRoute.meetId
    private val noticeId = meetingNoticeDetailRoute.noticeId

    private var commentsPagingJob: Job? = null

    private val noticeActionReceiver =
        noticeEventBus
            .action
            .actionStateIn(viewModelScope, NoticeAction.None)

    init {
        noticeActionReceiver
            .onEach { action ->
                intent {
                    when (action) {
                        is NoticeAction.NoticeUpdate -> {
                            applyNoticeUpdate(action.notice)
                        }

                        else -> {
                            return@intent
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    override suspend fun Syntax<MeetingNoticeDetailState, MeetingNoticeDetailSideEffect>.onContainerCreate() {
        loadNoticeDetail()
    }

    override fun onIntent(intent: Intent) {
        if (intent !is MeetingNoticeDetailIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is MeetingNoticeDetailIntent.BackClick -> {
                    postSideEffect(MeetingNoticeDetailSideEffect.NavigateToBack)
                }

                is MeetingNoticeDetailIntent.RefreshClick -> {
                    loadNoticeDetail()
                }

                is MeetingNoticeDetailIntent.NextCommentsPageLoad -> {
                    getComments(state.commentsPagingInfo.nextCursor)
                }

                is MeetingNoticeDetailIntent.CommentUploadClick -> {
                    uploadComment()
                }

                is MeetingNoticeDetailIntent.CommentWebLinkClick -> {
                    postSideEffect(MeetingNoticeDetailSideEffect.NavigateToWebBrowser(intent.webLink))
                }

                is MeetingNoticeDetailIntent.NoticeEditDialogShow -> {
                    reduce { state.copy(isShowNoticeEditDialog = intent.isShow) }
                }

                is MeetingNoticeDetailIntent.NoticeUpdateClick -> {
                    val noticeId = noticeId ?: return@intent
                    postSideEffect(MeetingNoticeDetailSideEffect.NavigateToMeetingNoticeWrite(meetId, noticeId))
                }

                is MeetingNoticeDetailIntent.NoticeDeleteClick -> {
                    deleteNotice()
                }

                is MeetingNoticeDetailIntent.CommentEditDialogShow -> {
                    reduce {
                        state.copy(
                            isShowCommentEditDialog = intent.isShow,
                            selectedComment = intent.comment,
                        )
                    }
                }

                is MeetingNoticeDetailIntent.CommentReportDialogShow -> {
                    reduce {
                        state.copy(
                            isShowCommentReportDialog = intent.isShow,
                            selectedComment = intent.comment,
                        )
                    }
                }

                is MeetingNoticeDetailIntent.CommentUpdateClick -> {
                    updateComment(intent.comment)
                }

                is MeetingNoticeDetailIntent.CommentDeleteClick -> {
                    deleteComment(intent.comment)
                }

                is MeetingNoticeDetailIntent.CommentReportClick -> {
                    reportComment(intent.comment)
                }
            }
        }
    }

    private fun loadNoticeDetail() {
        intent {
            reduce { state.copy(notice = Result.Loading, isNotFoundError = false) }

            val noticeId = noticeId
            if (noticeId == null) {
                val exception = NotFoundException(message = "noticeId is null", throwable = null)
                reduce { state.copy(notice = Result.Error(exception), isNotFoundError = true) }
                return@intent
            }

            try {
                val (user, notice, meeting) =
                    coroutineScope {
                        val userDeferred = async { userRepository.getUser().first() }
                        val noticeDeferred = async { noticeRepository.getNotice(noticeId) }
                        val meetingDeferred = async { meetingRepository.getMeeting(meetId) }
                        Triple(userDeferred.await(), noticeDeferred.await(), meetingDeferred.await())
                    }

                reduce {
                    state.copy(
                        user = user,
                        notice = Result.Success(notice.asUiModel()),
                        isHostUser = meeting.hostId == user.userId,
                    )
                }

                getComments()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val isNotFoundError = e is ForbiddenException || e is NotFoundException
                if (!isNotFoundError) crashReporter.logException(e)

                reduce { state.copy(notice = Result.Error(e), isNotFoundError = isNotFoundError) }
            }
        }
    }

    private fun getComments(cursor: String? = null) {
        if (commentsPagingJob.isActiveCheck()) return
        val noticeId = noticeId ?: return
        commentsPagingJob =
            intent {
                handleCommentsPagingData(
                    pagingData = null,
                    isLoading = true,
                    cursor = cursor,
                )

                val pagingData =
                    runCatching {
                        commentRepository.getNoticeComments(
                            noticeId = noticeId,
                            cursor = cursor ?: "",
                            size = 30,
                        )
                    }.getOrNull()

                if (!currentCoroutineContext().isActive) return@intent

                handleCommentsPagingData(
                    pagingData = pagingData,
                    isLoading = false,
                    cursor = cursor,
                )
            }
    }

    private suspend fun Syntax<MeetingNoticeDetailState, MeetingNoticeDetailSideEffect>.handleCommentsPagingData(
        pagingData: PaginationContainer<List<NoticeComment>>?,
        isLoading: Boolean,
        cursor: String?,
    ) {
        val result =
            PagingHelper.handlePagingResult(
                pagingData = pagingData,
                isLoading = isLoading,
                currentPagingInfo = state.commentsPagingInfo,
                currentItems = state.comments,
                isInitialLoad = cursor == null,
                transform = { items -> items.map { it.createNoticeCommentUiModel() } },
            )

        reduce {
            state.copy(
                commentsPagingInfo = result.pagingInfo,
                comments = result.items,
            )
        }
    }

    private suspend fun Syntax<MeetingNoticeDetailState, MeetingNoticeDetailSideEffect>.uploadComment() {
        val noticeId = noticeId ?: return
        val content = state.commentState.text.toString().trim()
        if (content.isEmpty()) return

        val updateComment = state.selectedUpdateComment

        setLoading(true)

        try {
            val newComment =
                if (updateComment == null) {
                    commentRepository.createNoticeComment(
                        noticeId = noticeId,
                        content = content,
                    )
                } else {
                    commentRepository.updateNoticeComment(
                        commentId = updateComment.commentId,
                        content = content,
                    )
                }.createNoticeCommentUiModel()

            state.commentState.clearText()

            if (updateComment == null) {
                reduce {
                    state.copy(
                        comments = listOf(newComment) + state.comments,
                        commentsPagingInfo =
                            state.commentsPagingInfo.copy(
                                totalCount = state.commentsPagingInfo.totalCount + 1,
                            ),
                    )
                }
            } else {
                reduce {
                    state.copy(
                        comments =
                            state.comments.map { uiModel ->
                                if (uiModel.comment.commentId == newComment.comment.commentId) newComment else uiModel
                            },
                        selectedUpdateComment = null,
                    )
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<MeetingNoticeDetailState, MeetingNoticeDetailSideEffect>.updateComment(comment: NoticeComment) {
        state.commentState.clearText()
        state.commentState.edit { insert(0, comment.content) }

        reduce { state.copy(selectedUpdateComment = comment) }
    }

    private suspend fun Syntax<MeetingNoticeDetailState, MeetingNoticeDetailSideEffect>.deleteComment(comment: NoticeComment) {
        setLoading(true)

        try {
            commentRepository.deleteComment(comment.commentId)

            if (state.selectedUpdateComment != null) state.commentState.clearText()

            reduce {
                state.copy(
                    comments = state.comments.filterNot { it.comment.commentId == comment.commentId },
                    commentsPagingInfo =
                        state.commentsPagingInfo.copy(
                            totalCount = (state.commentsPagingInfo.totalCount - 1).coerceAtLeast(0),
                        ),
                    selectedUpdateComment = null,
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<MeetingNoticeDetailState, MeetingNoticeDetailSideEffect>.reportComment(comment: NoticeComment) {
        setLoading(true)

        try {
            commentRepository.reportComment(commentId = comment.commentId)
            postSideEffect(MeetingNoticeDetailSideEffect.ShowToastMessage(ToastMessage.ReportCompletedMessage))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<MeetingNoticeDetailState, MeetingNoticeDetailSideEffect>.deleteNotice() {
        val noticeId = noticeId ?: return

        setLoading(true)

        try {
            noticeRepository.deleteNotice(noticeId = noticeId)
            noticeEventBus.send(NoticeAction.NoticeDelete(noticeId = noticeId))
            postSideEffect(MeetingNoticeDetailSideEffect.NavigateToBack)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<MeetingNoticeDetailState, MeetingNoticeDetailSideEffect>.applyNoticeUpdate(notice: Notice) {
        if (notice.noticeId != noticeId || !state.isSuccess) return

        reduce { state.copy(notice = Result.Success(notice.asUiModel())) }
    }

    private suspend fun Syntax<MeetingNoticeDetailState, MeetingNoticeDetailSideEffect>.showErrorToast(exception: Throwable) {
        val message = if (exception is IOException) ToastMessage.NetworkErrorMessage else ToastMessage.ServerErrorMessage
        postSideEffect(MeetingNoticeDetailSideEffect.ShowToastMessage(message))
    }

    @AssistedFactory
    interface Factory {
        fun create(meetingDetailRoute: DetailRoute.MeetingNoticeDetail): MeetingNoticeDetailViewModel
    }
}
