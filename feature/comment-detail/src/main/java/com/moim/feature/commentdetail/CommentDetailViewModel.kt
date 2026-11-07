package com.moim.feature.commentdetail

import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.insert
import com.moim.core.common.exception.ForbiddenException
import com.moim.core.common.exception.NotFoundException
import com.moim.core.common.model.Comment
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.User
import com.moim.core.common.model.isChild
import com.moim.core.data.datasource.comment.CommentRepository
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.eventbus.CommentAction
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.util.cancelIfActive
import com.moim.core.ui.util.createCommentUiModel
import com.moim.core.ui.util.createMentionTagMessage
import com.moim.core.ui.util.filterMentionedUsers
import com.moim.core.ui.util.isActiveCheck
import com.moim.core.ui.util.parseMentionTagMessage
import com.moim.core.ui.view.PagingHelper
import com.moim.core.ui.view.ToastMessage
import com.moim.feature.commentdetail.model.CommentDetailIntent
import com.moim.feature.commentdetail.model.CommentDetailSideEffect
import com.moim.feature.commentdetail.model.CommentDetailState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import org.orbitmvi.orbit.syntax.Syntax
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel(assistedFactory = CommentDetailViewModel.Factory::class)
class CommentDetailViewModel @AssistedInject constructor(
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
    private val meetingRepository: MeetingRepository,
    private val commentEventBus: EventBus<CommentAction>,
    @Assisted val commentDetailRoute: DetailRoute.CommentDetail,
) : MVIViewModel<CommentDetailState, CommentDetailSideEffect>(commentDetailRoute.asState()) {
    private var pagingJob: Job? = null
    private var searchJob: Job? = null
    private val comment = requireNotNull(commentDetailRoute.comment)
    private val meetingId = commentDetailRoute.meetId

    override suspend fun Syntax<CommentDetailState, CommentDetailSideEffect>.onContainerCreate() {
        val user = userRepository.getUser().first()
        val participants =
            runCatching {
                meetingRepository
                    .getMeetingParticipants(
                        meetingId = meetingId,
                        cursor = "",
                        size = 100,
                    ).content
            }.getOrDefault(emptyList())

        reduce { state.copy(user = user, meetingParticipants = participants) }
        getComments()
    }

    override fun onIntent(intent: Intent) {
        if (intent !is CommentDetailIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is CommentDetailIntent.BackClick -> {
                    postSideEffect(CommentDetailSideEffect.NavigateToBack)
                }

                is CommentDetailIntent.RefreshClick,
                is CommentDetailIntent.NextPageLoad,
                -> {
                    getComments(state.pagingInfo.nextCursor)
                }

                is CommentDetailIntent.MentionUserClick -> {
                    setSelectedUser(intent.user)
                }

                is CommentDetailIntent.UserProfileImageClick -> {
                    postSideEffect(
                        CommentDetailSideEffect.NavigateToImageViewerForUser(
                            image = intent.imageUrl,
                            userName = intent.userName,
                        ),
                    )
                }

                is CommentDetailIntent.CommentLikeClick -> {
                    setLikeComment(intent.comment)
                }

                is CommentDetailIntent.CommentReportClick -> {
                    reportComment(intent.comment)
                }

                is CommentDetailIntent.CommentUpdateClick -> {
                    updateComment(intent.comment)
                }

                is CommentDetailIntent.CommentDeleteClick -> {
                    deleteComment(intent.comment)
                }

                is CommentDetailIntent.CommentUploadClick -> {
                    uploadComment(intent.updateComment)
                }

                is CommentDetailIntent.CommentWebLinkClick -> {
                    postSideEffect(CommentDetailSideEffect.NavigateToWebBrowser(intent.webLink))
                }

                is CommentDetailIntent.MentionDialogShow -> {
                    showMentionDialog(intent.keyword)
                }

                is CommentDetailIntent.CommentEditDialogShow -> {
                    reduce {
                        state.copy(
                            isShowCommentEditDialog = intent.isShow,
                            selectedComment = intent.comment,
                        )
                    }
                }

                is CommentDetailIntent.CommentReportDialogShow -> {
                    reduce {
                        state.copy(
                            isShowCommentReportDialog = intent.isShow,
                            selectedComment = intent.comment,
                        )
                    }
                }
            }
        }
    }

    private fun getComments(cursor: String? = null) {
        if (pagingJob.isActiveCheck()) return
        pagingJob =
            intent {
                handlePagingData(
                    pagingData = null,
                    isLoading = true,
                    cursor = cursor,
                )

                val pagingData =
                    runCatching {
                        commentRepository.getReplyComments(
                            postId = comment.postId,
                            commentId = comment.commentId,
                            cursor = cursor ?: "",
                            size = 30,
                        )
                    }.onFailure {
                        when (it) {
                            is ForbiddenException,
                            is NotFoundException,
                            -> {
                                reduce { state.copy(isNotFoundError = true) }
                            }
                        }
                    }.getOrNull()

                handlePagingData(
                    pagingData = pagingData,
                    isLoading = false,
                    cursor = cursor,
                )
            }
    }

    private suspend fun Syntax<CommentDetailState, CommentDetailSideEffect>.handlePagingData(
        pagingData: PaginationContainer<List<Comment>>?,
        isLoading: Boolean,
        cursor: String?,
    ) {
        val result =
            PagingHelper.handlePagingResult(
                pagingData = pagingData,
                isLoading = isLoading,
                currentPagingInfo = state.pagingInfo,
                currentItems = state.replyComments,
                isInitialLoad = cursor == null,
                transform = { comments ->
                    comments.map { comment ->
                        comment.createCommentUiModel()
                    }
                },
            )

        reduce {
            state.copy(
                pagingInfo = result.pagingInfo,
                replyComments = result.items,
            )
        }
    }

    private suspend fun Syntax<CommentDetailState, CommentDetailSideEffect>.updateComment(comment: Comment) {
        val selectedMentions =
            comment.mentions.map {
                User(
                    userId = it.userId,
                    nickname = it.nickname,
                    profileUrl = it.imageUrl,
                )
            }
        val message =
            parseMentionTagMessage(
                mentionUsers = selectedMentions,
                message = comment.content,
            )

        state.commentState.clearText()
        state.commentState.edit { insert(0, message) }

        reduce {
            state.copy(
                selectedUpdateComment = comment,
                selectedMentions = selectedMentions,
            )
        }
    }

    private suspend fun Syntax<CommentDetailState, CommentDetailSideEffect>.setLikeComment(updateComment: Comment) {
        setLoading(true)

        try {
            val newComment = commentRepository.updateLikeComment(updateComment.commentId)

            if (newComment.isChild()) {
                reduce {
                    val newComments =
                        state.replyComments
                            .toMutableList()
                            .apply {
                                val targetComment = withIndex().first { it.value.commentId == newComment.commentId }
                                set(
                                    targetComment.index,
                                    targetComment.value.copy(comment = newComment),
                                )
                            }

                    state.copy(replyComments = newComments)
                }
            } else {
                val newParentComment = newComment.createCommentUiModel()

                reduce { state.copy(parentComment = newParentComment) }
                commentEventBus.send(CommentAction.CommentUpdate(commentUiModel = newParentComment))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<CommentDetailState, CommentDetailSideEffect>.uploadComment(updateComment: Comment?) {
        val isCreateComment = updateComment == null
        val tagMessage =
            createMentionTagMessage(
                mentionUsers = state.selectedMentions,
                message = state.commentState.text.toString(),
            )
        val selectedMentionUsers =
            filterMentionedUsers(
                mentionUsers = state.selectedMentions,
                message = tagMessage,
            )

        setLoading(true)

        try {
            val newComment =
                if (isCreateComment) {
                    commentRepository.createReplyComment(
                        postId = commentDetailRoute.postId,
                        commentId = state.parentComment.commentId,
                        content = tagMessage.trim(),
                        mentionIds = selectedMentionUsers.map { it.userId },
                    )
                } else {
                    commentRepository.updateComment(
                        commentId = updateComment.commentId,
                        content = tagMessage.trim(),
                        mentionIds = selectedMentionUsers.map { it.userId },
                    )
                }.createCommentUiModel()

            state.commentState.clearText()

            when {
                isCreateComment -> {
                    val parentComment = state.parentComment

                    commentEventBus.send(
                        CommentAction.CommentUpdate(
                            commentUiModel =
                                parentComment.copy(
                                    comment = parentComment.comment.copy(replayCount = parentComment.comment.replayCount.plus(1)),
                                ),
                        ),
                    )

                    reduce {
                        state.copy(
                            replyComments = state.replyComments + newComment,
                            selectedMentions = emptyList(),
                        )
                    }
                }

                newComment.comment.isChild() -> {
                    reduce {
                        val newComments =
                            state.replyComments
                                .toMutableList()
                                .apply {
                                    val targetComment = withIndex().first { it.value.commentId == newComment.comment.commentId }
                                    set(targetComment.index, newComment)
                                }

                        state.copy(
                            replyComments = newComments,
                            selectedUpdateComment = null,
                            selectedMentions = emptyList(),
                        )
                    }
                }

                else -> {
                    commentEventBus.send(CommentAction.CommentUpdate(commentUiModel = newComment))

                    reduce {
                        state.copy(
                            parentComment = newComment,
                            selectedUpdateComment = null,
                            selectedMentions = emptyList(),
                        )
                    }
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

    private suspend fun Syntax<CommentDetailState, CommentDetailSideEffect>.deleteComment(comment: Comment) {
        setLoading(true)

        try {
            commentRepository.deleteComment(comment.commentId)

            if (state.selectedUpdateComment != null) state.commentState.clearText()

            reduce {
                state.copy(
                    replyComments = state.replyComments.filterNot { it.commentId == comment.commentId },
                    selectedUpdateComment = null,
                )
            }

            if (comment.isChild()) {
                val parentComment = state.parentComment

                commentEventBus.send(
                    CommentAction.CommentUpdate(
                        commentUiModel =
                            parentComment.copy(
                                comment = parentComment.comment.copy(replayCount = parentComment.comment.replayCount.minus(1)),
                            ),
                    ),
                )
            } else {
                commentEventBus.send(CommentAction.CommentDelete(commentId = comment.commentId))
                postSideEffect(CommentDetailSideEffect.NavigateToBack)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<CommentDetailState, CommentDetailSideEffect>.reportComment(comment: Comment) {
        setLoading(true)

        try {
            commentRepository.reportComment(comment.commentId)
            postSideEffect(CommentDetailSideEffect.ShowToastMessage(ToastMessage.ReportCompletedMessage))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<CommentDetailState, CommentDetailSideEffect>.setSelectedUser(user: User) {
        val selectMentions = state.selectedMentions.toMutableList().apply { add(user) }.distinct()
        val mentionText =
            insertTextAtCursor(
                inputKeyword = user.nickname,
                currentMessage = state.commentState.text.toString(),
                currentSelection = state.commentState.selection.start,
            )

        state.commentState.clearText()
        state.commentState.edit { insert(0, mentionText) }

        reduce {
            state.copy(
                selectedMentions = selectMentions,
                searchMentions = emptyList(),
                isShowMentionDialog = false,
            )
        }
    }

    private fun insertTextAtCursor(
        inputKeyword: String,
        currentMessage: String,
        currentSelection: Int,
    ): String {
        val insertPosition = currentSelection.coerceIn(0, currentMessage.length)
        var matchLength = 0
        for (i in 1..minOf(insertPosition, inputKeyword.length)) {
            val startPos = insertPosition - i
            val substring = currentMessage.substring(startPos, insertPosition)

            if (inputKeyword.startsWith(substring)) {
                matchLength = i
            }
        }

        return if (matchLength > 0) {
            val beforeMatch = currentMessage.take(insertPosition - matchLength)
            val afterCursor = currentMessage.substring(insertPosition)
            "$beforeMatch$inputKeyword $afterCursor"
        } else {
            currentMessage.take(insertPosition) +
                inputKeyword + " " +
                currentMessage.substring(insertPosition)
        }
    }

    private fun showMentionDialog(keyword: String?) {
        searchJob.cancelIfActive()
        searchJob =
            intent {
                delay(400.milliseconds)

                val userList =
                    if (keyword != null) {
                        state.meetingParticipants.filter { it.nickname.contains(keyword) }
                    } else {
                        emptyList()
                    }

                reduce {
                    state.copy(
                        searchMentions = userList,
                        isShowMentionDialog = userList.isNotEmpty(),
                    )
                }
            }
    }

    private suspend fun Syntax<CommentDetailState, CommentDetailSideEffect>.showErrorToast(exception: Throwable) {
        val message = if (exception is IOException) ToastMessage.NetworkErrorMessage else ToastMessage.ServerErrorMessage
        postSideEffect(CommentDetailSideEffect.ShowToastMessage(message))
    }

    @AssistedFactory
    interface Factory {
        fun create(commentDetailRoute: DetailRoute.CommentDetail): CommentDetailViewModel
    }
}

private fun DetailRoute.CommentDetail.asState() =
    CommentDetailState(parentComment = requireNotNull(comment).createCommentUiModel())
