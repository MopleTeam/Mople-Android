package com.moim.feature.commentdetail

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.insert
import androidx.lifecycle.viewModelScope
import com.moim.core.common.exception.ForbiddenException
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.exception.NotFoundException
import com.moim.core.common.model.Comment
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.User
import com.moim.core.common.model.isChild
import com.moim.core.common.model.item.CommentUiModel
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.data.datasource.comment.CommentRepository
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.eventbus.CommentAction
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.util.cancelIfActive
import com.moim.core.ui.util.createCommentUiModel
import com.moim.core.ui.util.createMentionTagMessage
import com.moim.core.ui.util.filterMentionedUsers
import com.moim.core.ui.util.isActiveCheck
import com.moim.core.ui.util.parseMentionTagMessage
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.PagingHelper
import com.moim.core.ui.view.PagingUiState
import com.moim.core.ui.view.ToastMessage
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException

@HiltViewModel(assistedFactory = CommentDetailViewModel.Factory::class)
class CommentDetailViewModel @AssistedInject constructor(
    userRepository: UserRepository,
    private val commentRepository: CommentRepository,
    private val meetingRepository: MeetingRepository,
    private val commentEventBus: EventBus<CommentAction>,
    @Assisted val commentDetailRoute: DetailRoute.CommentDetail,
) : BaseViewModel() {
    private var pagingJob: Job? = null
    private var searchJob: Job? = null
    private val comment = requireNotNull(commentDetailRoute.comment)
    private val meetingId = commentDetailRoute.meetId

    init {
        viewModelScope.launch {
            setUiState(
                CommentDetailUiState(
                    user = userRepository.getUser().first(),
                    parentComment = comment.createCommentUiModel(),
                ),
            )

            uiState.checkState<CommentDetailUiState> {
                val participants =
                    runCatching {
                        meetingRepository
                            .getMeetingParticipants(
                                meetingId = meetingId,
                                cursor = "",
                                size = 100,
                            ).content
                    }.getOrDefault(emptyList())

                setUiState(copy(meetingParticipants = participants))
                getComments()
            }
        }
    }

    fun onUiAction(uiAction: CommentDetailUiAction) {
        when (uiAction) {
            is CommentDetailUiAction.OnClickBack -> {
                setUiEvent(CommentDetailUiEvent.NavigateToBack)
            }

            is CommentDetailUiAction.OnClickRefresh -> {
                val uiState = uiState.value as? CommentDetailUiState ?: return
                getComments(uiState.pagingInfo.nextCursor)
            }

            is CommentDetailUiAction.OnLoadNextPage -> {
                val uiState = uiState.value as? CommentDetailUiState ?: return
                getComments(uiState.pagingInfo.nextCursor)
            }

            is CommentDetailUiAction.OnClickMentionUser -> {
                setSelectedUser(uiAction.user)
            }

            is CommentDetailUiAction.OnClickUserProfileImage -> {
                navigateToImageViewerForUser(uiAction.imageUrl, uiAction.userName)
            }

            is CommentDetailUiAction.OnClickCommentLike -> {
                setLikeComment(uiAction.comment)
            }

            is CommentDetailUiAction.OnClickCommentReport -> {
                reportComment(uiAction.comment)
            }

            is CommentDetailUiAction.OnClickCommentUpdate -> {
                updateComment(uiAction.comment)
            }

            is CommentDetailUiAction.OnClickCommentDelete -> {
                deleteComment(uiAction.comment)
            }

            is CommentDetailUiAction.OnClickCommentUpload -> {
                uploadComment(uiAction.updateComment)
            }

            is CommentDetailUiAction.OnClickCommentWebLink -> {
                setUiEvent(CommentDetailUiEvent.NavigateToWebBrowser(uiAction.webLink))
            }

            is CommentDetailUiAction.OnShowMentionDialog -> {
                showMentionDialog(uiAction.keyword)
            }

            is CommentDetailUiAction.OnShowCommentEditDialog -> {
                showCommentEditDialog(uiAction.isShow, uiAction.comment)
            }

            is CommentDetailUiAction.OnShowCommentReportDialog -> {
                showCommentReportDialog(uiAction.isShow, uiAction.comment)
            }
        }
    }

    private fun getComments(cursor: String? = null) {
        if (pagingJob.isActiveCheck()) return
        pagingJob =
            viewModelScope.launch {
                uiState.checkState<CommentDetailUiState> {
                    handlePagingData(
                        pagingInfo = null,
                        isLoading = true,
                        cursor = cursor,
                    )

                    val pagingInfo =
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
                                    setUiState(copy(isNotFoundError = true))
                                }
                            }
                        }.getOrNull()

                    handlePagingData(
                        pagingInfo = pagingInfo,
                        isLoading = false,
                        cursor = cursor,
                    )
                }
            }
    }

    private fun handlePagingData(
        pagingInfo: PaginationContainer<List<Comment>>?,
        isLoading: Boolean,
        cursor: String?,
    ) {
        uiState.checkState<CommentDetailUiState> {
            val result =
                PagingHelper.handlePagingResult(
                    pagingData = pagingInfo,
                    isLoading = isLoading,
                    currentPagingInfo = this.pagingInfo,
                    currentItems = replyComments,
                    isInitialLoad = cursor == null,
                    transform = { comments ->
                        comments.map { comment ->
                            comment.createCommentUiModel()
                        }
                    },
                )

            setUiState(
                copy(
                    pagingInfo = result.pagingInfo,
                    replyComments = result.items,
                ),
            )
        }
    }

    private fun updateComment(comment: Comment) {
        uiState.checkState<CommentDetailUiState> {
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

            commentState.clearText()
            commentState.edit { insert(0, message) }
            setUiState(
                copy(
                    selectedUpdateComment = comment,
                    selectedMentions = selectedMentions,
                ),
            )
        }
    }

    private fun setLikeComment(updateComment: Comment) {
        viewModelScope.launch {
            commentRepository
                .updateLikeComment(updateComment.commentId)
                .asResult()
                .onEach { setLoading(it is Result.Loading) }
                .collect { result ->
                    uiState.checkState<CommentDetailUiState> {
                        when (result) {
                            is Result.Loading -> {
                                return@collect
                            }

                            is Result.Success -> {
                                val newComment = result.data
                                if (newComment.isChild()) {
                                    val newComments =
                                        replyComments
                                            .toMutableList()
                                            .apply {
                                                val targetComment = withIndex().first { it.value.commentId == newComment.commentId }
                                                set(
                                                    targetComment.index,
                                                    targetComment.value.copy(comment = newComment),
                                                )
                                            }

                                    setUiState(copy(replyComments = newComments))
                                } else {
                                    val newParentComment = newComment.createCommentUiModel()
                                    setUiState(copy(parentComment = newParentComment))
                                    commentEventBus.send(CommentAction.CommentUpdate(commentUiModel = newParentComment))
                                }
                            }

                            is Result.Error -> {
                                showErrorToast(result.exception)
                            }
                        }
                    }
                }
        }
    }

    private fun uploadComment(updateComment: Comment?) {
        viewModelScope.launch {
            uiState.checkState<CommentDetailUiState> {
                val isCreateComment = updateComment == null
                val tagMessage =
                    createMentionTagMessage(
                        mentionUsers = selectedMentions,
                        message = commentState.text.toString(),
                    )
                val selectedMentionUsers =
                    filterMentionedUsers(
                        mentionUsers = selectedMentions,
                        message = tagMessage,
                    )

                if (isCreateComment) {
                    commentRepository.createReplyComment(
                        postId = commentDetailRoute.postId,
                        commentId = parentComment.commentId,
                        content = tagMessage.trim(),
                        mentionIds = selectedMentionUsers.map { it.userId },
                    )
                } else {
                    commentRepository.updateComment(
                        commentId = updateComment.commentId,
                        content = tagMessage.trim(),
                        mentionIds = selectedMentionUsers.map { it.userId },
                    )
                }.asResult().onEach { setLoading(it is Result.Loading) }.collect { result ->
                    uiState.checkState<CommentDetailUiState> {
                        when (result) {
                            is Result.Loading -> {
                                return@collect
                            }

                            is Result.Success -> {
                                val newComment = result.data.createCommentUiModel()
                                commentState.clearText()

                                if (isCreateComment) {
                                    val comment = parentComment.comment
                                    val newComments =
                                        replyComments
                                            .toMutableList()
                                            .apply { add(newComment) }

                                    commentEventBus.send(
                                        CommentAction.CommentUpdate(
                                            commentUiModel =
                                                parentComment.copy(
                                                    comment = comment.copy(replayCount = comment.replayCount.plus(1)),
                                                ),
                                        ),
                                    )

                                    setUiState(
                                        copy(
                                            replyComments = newComments,
                                            selectedMentions = emptyList(),
                                        ),
                                    )
                                } else {
                                    if (newComment.comment.isChild()) {
                                        val newComments =
                                            replyComments
                                                .toMutableList()
                                                .apply {
                                                    val targetComment =
                                                        withIndex().first { it.value.commentId == newComment.comment.commentId }
                                                    set(targetComment.index, newComment)
                                                }
                                        setUiState(
                                            copy(
                                                replyComments = newComments,
                                                selectedUpdateComment = null,
                                                selectedMentions = emptyList(),
                                            ),
                                        )
                                    } else {
                                        commentEventBus.send(CommentAction.CommentUpdate(commentUiModel = newComment))
                                        setUiState(
                                            copy(
                                                parentComment = newComment,
                                                selectedUpdateComment = null,
                                                selectedMentions = emptyList(),
                                            ),
                                        )
                                    }
                                }
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

    private fun deleteComment(comment: Comment) {
        viewModelScope.launch {
            commentRepository
                .deleteComment(comment.commentId)
                .asResult()
                .onEach { setLoading(it is Result.Loading) }
                .collect { result ->
                    uiState.checkState<CommentDetailUiState> {
                        when (result) {
                            is Result.Loading -> {
                                return@collect
                            }

                            is Result.Success -> {
                                if (selectedUpdateComment != null) commentState.clearText()
                                val newComments =
                                    replyComments
                                        .toMutableList()
                                        .apply { removeIf { it.commentId == comment.commentId } }

                                setUiState(
                                    copy(
                                        replyComments = newComments,
                                        selectedUpdateComment = null,
                                    ),
                                )

                                if (comment.isChild()) {
                                    val replyCount = parentComment.comment.replayCount.minus(1)
                                    commentEventBus.send(
                                        CommentAction.CommentUpdate(
                                            commentUiModel =
                                                parentComment.copy(
                                                    comment = parentComment.comment.copy(replayCount = replyCount),
                                                ),
                                        ),
                                    )
                                } else {
                                    commentEventBus.send(CommentAction.CommentDelete(commentId = comment.commentId))
                                    setUiEvent(CommentDetailUiEvent.NavigateToBack)
                                }
                            }

                            is Result.Error -> {
                                showErrorToast(result.exception)
                            }
                        }
                    }
                }
        }
    }

    private fun reportComment(comment: Comment) {
        viewModelScope.launch {
            commentRepository
                .reportComment(commentId = comment.commentId)
                .asResult()
                .onEach { setLoading(it is Result.Loading) }
                .collect { result ->
                    uiState.checkState<CommentDetailUiState> {
                        when (result) {
                            is Result.Loading -> return@collect
                            is Result.Success -> setUiEvent(CommentDetailUiEvent.ShowToastMessage(ToastMessage.ReportCompletedMessage))
                            is Result.Error -> showErrorToast(result.exception)
                        }
                    }
                }
        }
    }

    private fun setSelectedUser(user: User) {
        uiState.checkState<CommentDetailUiState> {
            val selectMentions = selectedMentions.toMutableList().apply { add(user) }.distinct()
            val mentionText =
                insertTextAtCursor(
                    inputKeyword = user.nickname,
                    currentMessage = commentState.text.toString(),
                    currentSelection = commentState.selection.start,
                )

            commentState.clearText()
            commentState.edit { insert(0, mentionText) }

            setUiState(
                copy(
                    selectedMentions = selectMentions,
                    searchMentions = emptyList(),
                    isShowMentionDialog = false,
                ),
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
            viewModelScope.launch {
                delay(400)
                uiState.checkState<CommentDetailUiState> {
                    val userList =
                        if (keyword != null) {
                            meetingParticipants.filter { it.nickname.contains(keyword) }
                        } else {
                            emptyList()
                        }

                    setUiState(
                        copy(
                            searchMentions = userList,
                            isShowMentionDialog = userList.isNotEmpty(),
                        ),
                    )
                }
            }
    }

    private fun navigateToImageViewerForUser(
        userImageUrl: String,
        userName: String,
    ) {
        setUiEvent(
            CommentDetailUiEvent.NavigateToImageViewerForUser(
                image = userImageUrl,
                userName = userName,
            ),
        )
    }

    private fun showCommentEditDialog(
        isShow: Boolean,
        comment: Comment?,
    ) {
        uiState.checkState<CommentDetailUiState> {
            setUiState(copy(isShowCommentEditDialog = isShow, selectedComment = comment))
        }
    }

    private fun showCommentReportDialog(
        isShow: Boolean,
        comment: Comment?,
    ) {
        uiState.checkState<CommentDetailUiState> {
            setUiState(copy(isShowCommentReportDialog = isShow, selectedComment = comment))
        }
    }

    private fun showErrorToast(exception: Throwable) {
        when (exception) {
            is IOException -> setUiEvent(CommentDetailUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
            is NetworkException -> setUiEvent(CommentDetailUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(commentDetailRoute: DetailRoute.CommentDetail): CommentDetailViewModel
    }
}

data class CommentDetailUiState(
    val user: User,
    val parentComment: CommentUiModel,
    val replyComments: List<CommentUiModel> = emptyList(),
    val pagingInfo: PagingUiState = PagingUiState(),
    val isNotFoundError: Boolean = false,
    val commentState: TextFieldState = TextFieldState(),
    val meetingParticipants: List<User> = emptyList(),
    val searchMentions: List<User> = emptyList(),
    val selectedMentions: List<User> = emptyList(),
    val selectedComment: Comment? = null,
    val selectedUpdateComment: Comment? = null,
    val isShowCommentEditDialog: Boolean = false,
    val isShowCommentReportDialog: Boolean = false,
    val isShowMentionDialog: Boolean = false,
) : UiState

sealed interface CommentDetailUiAction : UiAction {
    data object OnClickBack : CommentDetailUiAction

    data object OnClickRefresh : CommentDetailUiAction

    data object OnLoadNextPage : CommentDetailUiAction

    data class OnClickMentionUser(
        val user: User,
    ) : CommentDetailUiAction

    data class OnClickUserProfileImage(
        val imageUrl: String,
        val userName: String,
    ) : CommentDetailUiAction

    data class OnClickCommentLike(
        val comment: Comment,
    ) : CommentDetailUiAction

    data class OnClickCommentReport(
        val comment: Comment,
    ) : CommentDetailUiAction

    data class OnClickCommentUpdate(
        val comment: Comment,
    ) : CommentDetailUiAction

    data class OnClickCommentDelete(
        val comment: Comment,
    ) : CommentDetailUiAction

    data class OnClickCommentUpload(
        val updateComment: Comment?,
    ) : CommentDetailUiAction

    data class OnClickCommentWebLink(
        val webLink: String,
    ) : CommentDetailUiAction

    data class OnShowMentionDialog(
        val keyword: String?,
    ) : CommentDetailUiAction

    data class OnShowCommentEditDialog(
        val isShow: Boolean,
        val comment: Comment?,
    ) : CommentDetailUiAction

    data class OnShowCommentReportDialog(
        val isShow: Boolean,
        val comment: Comment?,
    ) : CommentDetailUiAction
}

sealed interface CommentDetailUiEvent : UiEvent {
    data object NavigateToBack : CommentDetailUiEvent

    data class NavigateToImageViewerForUser(
        val image: String,
        val userName: String,
    ) : CommentDetailUiEvent

    data class NavigateToWebBrowser(
        val webLink: String,
    ) : CommentDetailUiEvent

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : CommentDetailUiEvent
}
