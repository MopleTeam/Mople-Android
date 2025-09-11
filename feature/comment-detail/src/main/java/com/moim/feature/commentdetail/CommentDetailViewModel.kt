package com.moim.feature.commentdetail

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.insert
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertHeaderItem
import androidx.paging.insertSeparators
import androidx.paging.map
import com.moim.core.common.delegate.CommentAction
import com.moim.core.common.delegate.CommentViewModelDelegate
import com.moim.core.common.delegate.commentStateIn
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.model.Comment
import com.moim.core.common.model.User
import com.moim.core.common.model.isChild
import com.moim.core.common.model.item.CommentUiModel
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.route.DetailRoute
import com.moim.core.common.util.cancelIfActive
import com.moim.core.common.util.createCommentUiModel
import com.moim.core.common.util.createMentionTagMessage
import com.moim.core.common.util.filterMentionedUsers
import com.moim.core.common.util.parseMentionTagMessage
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.ToastMessage
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.common.view.checkState
import com.moim.core.common.view.checkedActionedAtIsBeforeLoadedAt
import com.moim.core.common.view.restartableStateIn
import com.moim.core.data.datasource.comment.CommentRepository
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.domain.usecase.GetReplyCommentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CommentDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    userRepository: UserRepository,
    private val commentRepository: CommentRepository,
    private val meetingRepository: MeetingRepository,
    private val getReplyCommentUseCase: GetReplyCommentUseCase,
    commentViewModelDelegate: CommentViewModelDelegate,
) : BaseViewModel(),
    CommentViewModelDelegate by commentViewModelDelegate {

    private val meetingDetailArgs
        get() = savedStateHandle.toRoute<DetailRoute.CommentDetail>(DetailRoute.CommentDetail.typeMap)

    private val meetId = meetingDetailArgs.meetId
    private val postId = meetingDetailArgs.postId
    private val comment = requireNotNull(meetingDetailArgs.comment)
    private var searchJob: Job? = null

    private val commentActionReceiver = commentAction.commentStateIn(viewModelScope)

    private var _comments = flowOf(postId)
        .flatMapLatest { postId ->
            getReplyCommentUseCase(
                GetReplyCommentUseCase.Params(
                    postId = postId,
                    commentId = comment.commentId
                )
            )
        }
        .mapLatest { it.map { comment -> comment.createCommentUiModel() } }
        .mapLatest { it.insertHeaderItem(item = comment.createCommentUiModel()) }
        .cachedIn(viewModelScope)
    private val comments = commentActionReceiver.flatMapLatest { receiver ->
        when (receiver) {
            is CommentAction.None -> _comments

            is CommentAction.CommentCreate -> {
                _comments.map { pagingData ->
                    pagingData.checkedActionedAtIsBeforeLoadedAt(
                        actionedAt = receiver.actionAt,
                        loadedAt = getReplyCommentUseCase.loadedAt
                    ) {
                        pagingData.insertSeparators { before: CommentUiModel?, _: CommentUiModel? ->
                            if (before?.comment?.commentId == comment.commentId) {
                                return@insertSeparators receiver.commentUiModel
                            } else {
                                null
                            }
                        }
                    }
                }
            }

            is CommentAction.CommentUpdate -> {
                _comments.map { pagingData ->
                    pagingData.checkedActionedAtIsBeforeLoadedAt(
                        actionedAt = receiver.actionAt,
                        loadedAt = getReplyCommentUseCase.loadedAt
                    ) {
                        pagingData.map { uiModel ->
                            if (uiModel.comment.commentId == receiver.commentUiModel.comment.commentId) {
                                receiver.commentUiModel
                            } else {
                                uiModel
                            }
                        }
                    }
                }
            }

            is CommentAction.CommentDelete -> {
                _comments.map { pagingData ->
                    pagingData.checkedActionedAtIsBeforeLoadedAt(
                        actionedAt = receiver.actionAt,
                        loadedAt = getReplyCommentUseCase.loadedAt
                    ) {
                        pagingData
                            .map { uiModel ->
                                if (uiModel.comment.commentId == receiver.commentId) {
                                    uiModel.apply { isDeleted = true }
                                } else {
                                    uiModel
                                }
                            }
                            .filter { it.isDeleted.not() }
                    }
                }
            }
        }.also {
            _comments = it
        }
    }.cachedIn(viewModelScope)

    private val meetingParticipants = flowOf(meetId)
        .mapLatest {
            meetingRepository.getMeetingParticipants(
                meetingId = it,
                cursor = "",
                size = 100
            ).content
        }.asResult().mapLatest {
            if (it is Result.Success) {
                it.data
            } else {
                null
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)


    private val commentDetailUiState =
        userRepository
            .getUser()
            .mapLatest { user ->
                CommentDetailUiState.Success(
                    user = user,
                    parentComment = comment.createCommentUiModel()
                )
            }.asResult()
            .mapLatest { result ->
                when (result) {
                    is Result.Loading -> CommentDetailUiState.Loading
                    is Result.Success -> result.data
                    is Result.Error -> CommentDetailUiState.Error
                }
            }.restartableStateIn(viewModelScope, SharingStarted.Lazily, CommentDetailUiState.Loading)

    init {
        viewModelScope.launch {
            launch {
                commentDetailUiState.collect { uiState ->
                    setUiState(uiState)
                    if (uiState is CommentDetailUiState.Success) {
                        setUiState(uiState.copy(replyComments = comments))
                    }
                }
            }

            launch {
                meetingParticipants.filterNotNull().collect {
                    uiState.checkState<CommentDetailUiState.Success> {
                        setUiState(uiState = copy(meetingParticipants = it))
                    }
                }
            }
        }
    }

    fun onUiAction(uiAction: CommentDetailAction) {
        when (uiAction) {
            is CommentDetailAction.OnClickBack -> setUiEvent(CommentDetailUiEvent.NavigateToBack)
            is CommentDetailAction.OnClickRefresh -> commentDetailUiState.restart()
            is CommentDetailAction.OnClickMentionUser -> setSelectedUser(uiAction.user)
            is CommentDetailAction.OnClickUserProfileImage -> navigateToImageViewerForUser(uiAction.imageUrl, uiAction.userName)
            is CommentDetailAction.OnClickCommentLike -> setLikeComment(uiAction.comment)
            is CommentDetailAction.OnClickCommentReport -> reportComment(uiAction.comment)
            is CommentDetailAction.OnClickCommentUpdate -> updateComment(uiAction.comment)
            is CommentDetailAction.OnClickCommentDelete -> deleteComment(uiAction.comment)
            is CommentDetailAction.OnClickCommentUpload -> uploadComment(uiAction.updateComment)
            is CommentDetailAction.OnClickCommentWebLink -> setUiEvent(CommentDetailUiEvent.NavigateToWebBrowser(uiAction.webLink))
            is CommentDetailAction.OnShowMentionDialog -> showMentionDialog(uiAction.keyword)
            is CommentDetailAction.OnShowCommentEditDialog -> showCommentEditDialog(uiAction.isShow, uiAction.comment)
            is CommentDetailAction.OnShowCommentReportDialog -> showCommentReportDialog(uiAction.isShow, uiAction.comment)
        }
    }

    private fun updateComment(comment: Comment) {
        uiState.checkState<CommentDetailUiState.Success> {
            val selectedMentions = comment.mentions.map {
                User(
                    userId = it.userId,
                    nickname = it.nickname,
                    profileUrl = it.imageUrl
                )
            }
            val message = parseMentionTagMessage(
                mentionUsers = selectedMentions,
                message = comment.content
            )

            commentState.clearText()
            commentState.edit { insert(0, message) }
            setUiState(
                copy(
                    selectedUpdateComment = comment,
                    selectedMentions = selectedMentions
                )
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
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> updatePlanComment(commentUiModel = result.data.createCommentUiModel())
                        is Result.Error -> showErrorToast(result.exception)
                    }
                }
        }
    }

    private fun uploadComment(updateComment: Comment?) {
        viewModelScope.launch {
            uiState.checkState<CommentDetailUiState.Success> {
                val tagMessage = createMentionTagMessage(
                    mentionUsers = selectedMentions,
                    message = commentState.text.toString()
                )
                val selectedMentionUsers = filterMentionedUsers(
                    mentionUsers = selectedMentions,
                    message = tagMessage
                )
                if (updateComment == null) {
                    commentRepository.createReplyComment(
                        postId = postId,
                        commentId = parentComment.comment.commentId,
                        content = tagMessage.trim(),
                        mentionIds = selectedMentionUsers.map { it.userId }
                    )
                } else {
                    commentRepository.updateComment(
                        commentId = updateComment.commentId,
                        content = tagMessage.trim(),
                        mentionIds = selectedMentionUsers.map { it.userId }
                    )
                }.asResult().onEach { setLoading(it is Result.Loading) }.collect { result ->
                    uiState.checkState<CommentDetailUiState.Success> {
                        when (result) {
                            is Result.Loading -> return@collect

                            is Result.Success -> {
                                if (updateComment == null) {
                                    val comment = parentComment.comment
                                    createPlanComment(commentUiModel = result.data.createCommentUiModel())
                                    updatePlanComment(commentUiModel = parentComment.copy(comment = comment.copy(replayCount = comment.replayCount.plus(1))))
                                    commentState.clearText()
                                    setUiState(copy(selectedMentions = emptyList()))
                                } else {
                                    updatePlanComment(commentUiModel = result.data.createCommentUiModel())
                                    commentState.clearText()
                                    setUiState(
                                        copy(
                                            selectedUpdateComment = null,
                                            selectedMentions = emptyList(),
                                        )
                                    )
                                }
                            }

                            is Result.Error -> showErrorToast(result.exception)
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
                    uiState.checkState<CommentDetailUiState.Success> {
                        when (result) {
                            is Result.Loading -> return@collect

                            is Result.Success -> {
                                deletePlanComment(commentId = comment.commentId)

                                if (selectedUpdateComment != null) commentState.clearText()
                                setUiState(copy(selectedUpdateComment = null))

                                if (comment.isChild().not()) {
                                    setUiEvent(CommentDetailUiEvent.NavigateToBack)
                                } else {
                                    val replyCount = parentComment.comment.replayCount.minus(1)
                                    updatePlanComment(commentUiModel = parentComment.copy(parentComment.comment.copy(replayCount = replyCount)))
                                }
                            }

                            is Result.Error -> showErrorToast(result.exception)
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
                    uiState.checkState<CommentDetailUiState.Success> {
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
        uiState.checkState<CommentDetailUiState.Success> {
            val selectMentions = selectedMentions.toMutableList().apply { add(user) }.distinct()
            val mentionText = insertTextAtCursor(
                inputKeyword = user.nickname,
                currentMessage = commentState.text.toString(),
                currentSelection = commentState.selection.start
            )

            commentState.clearText()
            commentState.edit { insert(0, mentionText) }

            setUiState(
                copy(
                    selectedMentions = selectMentions,
                    searchMentions = emptyList(),
                    isShowMentionDialog = false
                )
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
            val beforeMatch = currentMessage.substring(0, insertPosition - matchLength)
            val afterCursor = currentMessage.substring(insertPosition)
            "$beforeMatch$inputKeyword $afterCursor"
        } else {
            currentMessage.substring(0, insertPosition) +
                    inputKeyword + " " +
                    currentMessage.substring(insertPosition)
        }
    }

    private fun showMentionDialog(keyword: String?) {
        searchJob.cancelIfActive()
        searchJob = viewModelScope.launch {
            delay(400)
            uiState.checkState<CommentDetailUiState.Success> {
                val userList = if (keyword != null) {
                    meetingParticipants.filter { it.nickname.contains(keyword) }
                } else {
                    emptyList()
                }

                setUiState(
                    copy(
                        searchMentions = userList,
                        isShowMentionDialog = userList.isNotEmpty()
                    )
                )
            }
        }
    }

    private fun navigateToImageViewerForUser(userImageUrl: String, userName: String) {
        setUiEvent(
            CommentDetailUiEvent.NavigateToImageViewerForUser(
                image = userImageUrl,
                userName = userName
            )
        )
    }

    private fun showCommentEditDialog(isShow: Boolean, comment: Comment?) {
        uiState.checkState<CommentDetailUiState.Success> {
            setUiState(copy(isShowCommentEditDialog = isShow, selectedComment = comment))
        }
    }

    private fun showCommentReportDialog(isShow: Boolean, comment: Comment?) {
        uiState.checkState<CommentDetailUiState.Success> {
            setUiState(copy(isShowCommentReportDialog = isShow, selectedComment = comment))
        }
    }

    private fun showErrorToast(exception: Throwable) {
        when (exception) {
            is IOException -> setUiEvent(CommentDetailUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
            is NetworkException -> setUiEvent(CommentDetailUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
        }
    }
}

sealed interface CommentDetailUiState : UiState {
    data object Loading : CommentDetailUiState

    data class Success(
        val user: User,
        val parentComment: CommentUiModel,
        val commentState: TextFieldState = TextFieldState(),
        val replyComments: Flow<PagingData<CommentUiModel>>? = null,
        val meetingParticipants: List<User> = emptyList(),
        val searchMentions: List<User> = emptyList(),
        val selectedMentions: List<User> = emptyList(),
        val selectedComment: Comment? = null,
        val selectedUpdateComment: Comment? = null,
        val isShowCommentEditDialog: Boolean = false,
        val isShowCommentReportDialog: Boolean = false,
        val isShowMentionDialog: Boolean = false,
    ) : CommentDetailUiState

    data object Error : CommentDetailUiState
}

sealed interface CommentDetailAction : UiAction {
    data object OnClickBack : CommentDetailAction
    data object OnClickRefresh : CommentDetailAction
    data class OnClickMentionUser(val user: User) : CommentDetailAction
    data class OnClickUserProfileImage(val imageUrl: String, val userName: String) : CommentDetailAction
    data class OnClickCommentLike(val comment: Comment) : CommentDetailAction
    data class OnClickCommentReport(val comment: Comment) : CommentDetailAction
    data class OnClickCommentUpdate(val comment: Comment) : CommentDetailAction
    data class OnClickCommentDelete(val comment: Comment) : CommentDetailAction
    data class OnClickCommentUpload(val updateComment: Comment?) : CommentDetailAction
    data class OnClickCommentWebLink(val webLink: String) : CommentDetailAction
    data class OnShowMentionDialog(val keyword: String?) : CommentDetailAction
    data class OnShowCommentEditDialog(val isShow: Boolean, val comment: Comment?) : CommentDetailAction
    data class OnShowCommentReportDialog(val isShow: Boolean, val comment: Comment?) : CommentDetailAction
}

sealed interface CommentDetailUiEvent : UiEvent {
    data object NavigateToBack : CommentDetailUiEvent

    data class NavigateToImageViewerForUser(
        val image: String,
        val userName: String
    ) : CommentDetailUiEvent

    data class NavigateToWebBrowser(
        val webLink: String
    ) : CommentDetailUiEvent

    data class ShowToastMessage(
        val message: ToastMessage
    ) : CommentDetailUiEvent
}