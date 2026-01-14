package com.moim.feature.plandetail

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.insert
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertSeparators
import androidx.paging.map
import com.moim.core.common.exception.ForbiddenException
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.exception.NotFoundException
import com.moim.core.common.model.Comment
import com.moim.core.common.model.User
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.model.isChild
import com.moim.core.common.model.item.CommentUiModel
import com.moim.core.common.model.item.PlanItem
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.data.datasource.comment.CommentRepository
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.domain.usecase.GetCommentsUseCase
import com.moim.core.domain.usecase.GetPlanItemUseCase
import com.moim.core.ui.eventbus.CommentAction
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.PlanAction
import com.moim.core.ui.eventbus.actionStateIn
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.util.cancelIfActive
import com.moim.core.ui.util.createCommentUiModel
import com.moim.core.ui.util.createMentionTagMessage
import com.moim.core.ui.util.filterMentionedUsers
import com.moim.core.ui.util.parseMentionTagMessage
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.ToastMessage
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import com.moim.core.ui.view.checkedActionedAtIsBeforeLoadedAt
import com.moim.core.ui.view.restartableStateIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.ZonedDateTime

@HiltViewModel(assistedFactory = PlanDetailViewModel.Factory::class)
class PlanDetailViewModel @AssistedInject constructor(
    private val savedStateHandle: SavedStateHandle,
    userRepository: UserRepository,
    getPlanItemUseCase: GetPlanItemUseCase,
    private val planRepository: PlanRepository,
    private val meetingRepository: MeetingRepository,
    private val reviewRepository: ReviewRepository,
    private val commentRepository: CommentRepository,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val planEventBus: EventBus<PlanAction>,
    private val commentEventBus: EventBus<CommentAction>,
    @Assisted val planDetailRoute: DetailRoute.PlanDetail,
) : BaseViewModel() {
    private val viewIdType = planDetailRoute.viewIdType
    private val meetId = savedStateHandle.getStateFlow<String?>(KEY_MEET_ID, null)
    private val commentCheckId = savedStateHandle.getStateFlow<String?>(key = KEY_COMMENT_CHECK_ID, null)

    private var searchJob: Job? = null

    private val planItemReceiver =
        planEventBus
            .action
            .actionStateIn(viewModelScope, PlanAction.None)
    private val commentActionReceiver =
        commentEventBus
            .action
            .actionStateIn(viewModelScope, CommentAction.None)

    private var _comments =
        commentCheckId
            .filterNotNull()
            .flatMapLatest { commentCheckId ->
                getCommentsUseCase(GetCommentsUseCase.Params(commentCheckId)).mapLatest {
                    it.map { comment -> comment.createCommentUiModel() }
                }
            }.cachedIn(viewModelScope)

    private val comments =
        commentActionReceiver
            .flatMapLatest { receiver ->
                when (receiver) {
                    is CommentAction.None -> {
                        _comments
                    }

                    is CommentAction.CommentCreate -> {
                        _comments.map { pagingData ->
                            pagingData.checkedActionedAtIsBeforeLoadedAt(
                                actionedAt = receiver.actionAt,
                                loadedAt = getCommentsUseCase.loadedAt,
                            ) {
                                pagingData.insertSeparators { before: CommentUiModel?, _: CommentUiModel? ->
                                    if (receiver.commentUiModel.comment.isChild()) {
                                        return@insertSeparators null
                                    } else if (before == null) {
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
                                loadedAt = getCommentsUseCase.loadedAt,
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
                                loadedAt = getCommentsUseCase.loadedAt,
                            ) {
                                pagingData
                                    .map { uiModel ->
                                        if (uiModel.comment.commentId == receiver.commentId) {
                                            uiModel.apply { isDeleted = true }
                                        } else {
                                            uiModel
                                        }
                                    }.filter { it.isDeleted.not() }
                            }
                        }
                    }
                }.also {
                    _comments = it
                }
            }.cachedIn(viewModelScope)

    private val meetingParticipants =
        meetId
            .filterNotNull()
            .mapLatest {
                meetingRepository
                    .getMeetingParticipants(
                        meetingId = it,
                        cursor = "",
                        size = 100,
                    ).content
            }.asResult()
            .mapLatest {
                if (it is Result.Success) {
                    it.data
                } else {
                    null
                }
            }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val planDetailUiState =
        combine(
            userRepository.getUser(),
            getPlanItemUseCase(GetPlanItemUseCase.Params(viewIdType)),
            ::Pair,
        ).mapLatest { (user, post) ->
            val isShowApplyButton = post.planAt.isAfter(ZonedDateTime.now()) && user.userId != post.userId
            PlanDetailUiState.Success(
                user = user,
                planItem = post,
                isShowApplyButton = isShowApplyButton,
            )
        }.asResult()
            .mapLatest { result ->
                when (result) {
                    is Result.Loading -> {
                        PlanDetailUiState.Loading
                    }

                    is Result.Success -> {
                        result.data
                    }

                    is Result.Error -> {
                        when (result.exception) {
                            is ForbiddenException,
                            is NotFoundException,
                            -> PlanDetailUiState.NotFoundError

                            else -> PlanDetailUiState.CommonError
                        }
                    }
                }
            }.restartableStateIn(viewModelScope, SharingStarted.Lazily, PlanDetailUiState.Loading)

    init {
        viewModelScope.launch {
            launch {
                planDetailUiState.collect { uiState ->
                    setUiState(uiState)
                    if (uiState is PlanDetailUiState.Success) {
                        savedStateHandle[KEY_COMMENT_CHECK_ID] = uiState.planItem.commentCheckId
                        savedStateHandle[KEY_MEET_ID] = uiState.planItem.meetingId
                        setUiState(uiState.copy(comments = comments))
                    }
                }
            }

            launch {
                meetingParticipants.filterNotNull().collect {
                    uiState.checkState<PlanDetailUiState.Success> {
                        setUiState(uiState = copy(meetingParticipants = it))
                    }
                }
            }

            launch {
                planItemReceiver.collect { action ->
                    when (action) {
                        is PlanAction.PlanUpdate -> {
                            uiState.checkState<PlanDetailUiState.Success> {
                                setUiState(copy(planItem = action.planItem))
                            }
                        }

                        is PlanAction.PlanInvalidate -> {
                            planDetailUiState.restart()
                        }

                        else -> {
                            return@collect
                        }
                    }
                }
            }
        }
    }

    fun onUiAction(uiAction: PlanDetailUiAction) {
        when (uiAction) {
            is PlanDetailUiAction.OnClickBack -> setUiEvent(PlanDetailUiEvent.NavigateToBack)
            is PlanDetailUiAction.OnClickRefresh -> planDetailUiState.restart()
            is PlanDetailUiAction.OnClickParticipants -> navigateToParticipants()
            is PlanDetailUiAction.OnClickPlanUpdate -> navigateToPlanWrite()
            is PlanDetailUiAction.OnClickPlanDelete -> deletePlan()
            is PlanDetailUiAction.OnClickPlanReport -> reportPlan()
            is PlanDetailUiAction.OnClickPlanApply -> planApply(uiAction.isApply)
            is PlanDetailUiAction.OnClickMapDetail -> navigateToMapDetail()
            is PlanDetailUiAction.OnClickCommentLike -> setLikeComment(uiAction.comment)
            is PlanDetailUiAction.OnClickCommentAddReply -> navigateToCommentDetail(uiAction.comment)
            is PlanDetailUiAction.OnClickCommentUpload -> uploadComment(uiAction.updateComment)
            is PlanDetailUiAction.OnClickCommentReport -> reportComment(uiAction.comment)
            is PlanDetailUiAction.OnClickCommentUpdate -> updateComment(uiAction.comment)
            is PlanDetailUiAction.OnClickCommentDelete -> deleteComment(uiAction.comment)
            is PlanDetailUiAction.OnClickCommentWebLink -> setUiEvent(PlanDetailUiEvent.NavigateToWebBrowser(uiAction.webLink))
            is PlanDetailUiAction.OnClickReviewImage -> navigateToImageViewerForReview(uiAction.selectedImageIndex)
            is PlanDetailUiAction.OnClickUserProfileImage -> navigateToImageViewerForUser(uiAction.imageUrl, uiAction.userName)
            is PlanDetailUiAction.OnClickMentionUser -> setSelectedUser(uiAction.user)
            is PlanDetailUiAction.OnShowMentionDialog -> showMentionDialog(uiAction.keyword)
            is PlanDetailUiAction.OnShowPlanApplyCancelDialog -> showApplyCancelDialog(uiAction.isShow)
            is PlanDetailUiAction.OnShowPlanEditDialog -> showPlanEditDialog(uiAction.isShow)
            is PlanDetailUiAction.OnShowPlanReportDialog -> showPlanReportDialog(uiAction.isShow)
            is PlanDetailUiAction.OnShowCommentEditDialog -> showCommentEditDialog(uiAction.isShow, uiAction.comment)
            is PlanDetailUiAction.OnShowCommentReportDialog -> showCommentReportDialog(uiAction.isShow, uiAction.comment)
        }
    }

    private fun updateComment(comment: Comment) {
        uiState.checkState<PlanDetailUiState.Success> {
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

    private fun planApply(isApply: Boolean) {
        viewModelScope.launch {
            uiState.checkState<PlanDetailUiState.Success> {
                if (isApply) {
                    planRepository.joinPlan(viewIdType.id)
                } else {
                    planRepository.leavePlan(viewIdType.id)
                }.asResult().onEach { setLoading(it is Result.Loading) }.collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            return@collect
                        }

                        is Result.Success -> {
                            val planItem = planItem.copy(isParticipant = isApply)
                            planEventBus.send(PlanAction.PlanUpdate(planItem = planItem))
                            setUiState(
                                copy(
                                    planItem = planItem,
                                    isShowApplyCancelDialog = false,
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

    private fun reportPlan() {
        viewModelScope.launch {
            uiState.checkState<PlanDetailUiState.Success> {
                if (planItem.isPlanAtBefore) {
                    planRepository.reportPlan(planId = planItem.postId)
                } else {
                    reviewRepository.reportReview(reviewId = planItem.postId)
                }.asResult().onEach { setLoading(it is Result.Loading) }.collect { result ->
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> setUiEvent(PlanDetailUiEvent.ShowToastMessage(ToastMessage.ReportCompletedMessage))
                        is Result.Error -> showErrorToast(result.exception)
                    }
                }
            }
        }
    }

    private fun deletePlan() {
        viewModelScope.launch {
            uiState.checkState<PlanDetailUiState.Success> {
                if (planItem.isPlanAtBefore) {
                    planRepository.deletePlan(planId = planItem.postId)
                } else {
                    reviewRepository.deleteReview(reviewId = planItem.postId)
                }.asResult().onEach { setLoading(it is Result.Loading) }.collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            return@collect
                        }

                        is Result.Success -> {
                            planEventBus.send(PlanAction.PlanDelete(postId = planItem.postId))
                            setUiEvent(PlanDetailUiEvent.NavigateToBack)
                        }

                        is Result.Error -> {
                            showErrorToast(result.exception)
                        }
                    }
                }
            }
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
                        is Result.Loading -> {
                            return@collect
                        }

                        is Result.Success -> {
                            commentEventBus.send(CommentAction.CommentUpdate(commentUiModel = result.data.createCommentUiModel()))
                        }

                        is Result.Error -> {
                            showErrorToast(result.exception)
                        }
                    }
                }
        }
    }

    private fun uploadComment(updateComment: Comment?) {
        viewModelScope.launch {
            uiState.checkState<PlanDetailUiState.Success> {
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

                if (updateComment == null) {
                    commentRepository.createComment(
                        postId = planItem.commentCheckId,
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
                    uiState.checkState<PlanDetailUiState.Success> {
                        when (result) {
                            is Result.Loading -> {
                                return@collect
                            }

                            is Result.Success -> {
                                if (updateComment == null) {
                                    commentEventBus.send(CommentAction.CommentCreate(commentUiModel = result.data.createCommentUiModel()))
                                    commentState.clearText()
                                    setUiState(
                                        copy(
                                            planItem = planItem.copy(commentCount = planItem.commentCount.plus(1)),
                                            selectedMentions = emptyList(),
                                        ),
                                    )
                                } else {
                                    commentEventBus.send(CommentAction.CommentUpdate(commentUiModel = result.data.createCommentUiModel()))
                                    commentState.clearText()
                                    setUiState(
                                        copy(
                                            selectedUpdateComment = null,
                                            selectedMentions = emptyList(),
                                        ),
                                    )
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
                    uiState.checkState<PlanDetailUiState.Success> {
                        when (result) {
                            is Result.Loading -> {
                                return@collect
                            }

                            is Result.Success -> {
                                commentEventBus.send(CommentAction.CommentDelete(commentId = comment.commentId))
                                if (selectedUpdateComment != null) {
                                    commentState.clearText()
                                }
                                setUiState(
                                    copy(
                                        planItem = planItem.copy(commentCount = planItem.commentCount.minus(1)),
                                        selectedUpdateComment = null,
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

    private fun reportComment(comment: Comment) {
        viewModelScope.launch {
            commentRepository
                .reportComment(commentId = comment.commentId)
                .asResult()
                .onEach { setLoading(it is Result.Loading) }
                .collect { result ->
                    uiState.checkState<PlanDetailUiState.Success> {
                        when (result) {
                            is Result.Loading -> return@collect
                            is Result.Success -> setUiEvent(PlanDetailUiEvent.ShowToastMessage(ToastMessage.ReportCompletedMessage))
                            is Result.Error -> showErrorToast(result.exception)
                        }
                    }
                }
        }
    }

    private fun setSelectedUser(user: User) {
        uiState.checkState<PlanDetailUiState.Success> {
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
                uiState.checkState<PlanDetailUiState.Success> {
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

    private fun showApplyCancelDialog(isShow: Boolean) {
        uiState.checkState<PlanDetailUiState.Success> {
            setUiState(copy(isShowApplyCancelDialog = isShow))
        }
    }

    private fun showPlanEditDialog(isShow: Boolean) {
        uiState.checkState<PlanDetailUiState.Success> {
            setUiState(copy(isShowPlanEditDialog = isShow))
        }
    }

    private fun showPlanReportDialog(isShow: Boolean) {
        uiState.checkState<PlanDetailUiState.Success> {
            setUiState(copy(isShowPlanReportDialog = isShow))
        }
    }

    private fun showCommentEditDialog(
        isShow: Boolean,
        comment: Comment?,
    ) {
        uiState.checkState<PlanDetailUiState.Success> {
            setUiState(copy(isShowCommentEditDialog = isShow, selectedComment = comment))
        }
    }

    private fun showCommentReportDialog(
        isShow: Boolean,
        comment: Comment?,
    ) {
        uiState.checkState<PlanDetailUiState.Success> {
            setUiState(copy(isShowCommentReportDialog = isShow, selectedComment = comment))
        }
    }

    private fun showErrorToast(exception: Throwable) {
        when (exception) {
            is IOException -> setUiEvent(PlanDetailUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
            is NetworkException -> setUiEvent(PlanDetailUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
        }
    }

    private fun navigateToPlanWrite() {
        uiState.checkState<PlanDetailUiState.Success> {
            if (planItem.isPlanAtBefore) {
                setUiEvent(PlanDetailUiEvent.NavigateToPlanWrite(planItem))
            } else {
                setUiEvent(PlanDetailUiEvent.NavigateToReviewWrite(planItem.postId))
            }
        }
    }

    private fun navigateToMapDetail() {
        uiState.checkState<PlanDetailUiState.Success> {
            setUiEvent(
                PlanDetailUiEvent.NavigateToMapDetail(
                    placeName = planItem.placeName,
                    address = planItem.loadAddress,
                    latitude = planItem.latitude,
                    longitude = planItem.longitude,
                ),
            )
        }
    }

    private fun navigateToParticipants() {
        uiState.checkState<PlanDetailUiState.Success> {
            val viewIdType =
                if (planItem.isPlanAtBefore) {
                    ViewIdType.PlanId(planItem.postId)
                } else {
                    ViewIdType.ReviewId(planItem.postId)
                }

            setUiEvent(PlanDetailUiEvent.NavigateToParticipants(viewIdType))
        }
    }

    private fun navigateToImageViewerForReview(selectedImageIndex: Int) {
        uiState.checkState<PlanDetailUiState.Success> {
            setUiEvent(
                PlanDetailUiEvent.NavigateToImageViewerForReview(
                    images = this.planItem.reviewImages.map { it.imageUrl },
                    position = selectedImageIndex,
                ),
            )
        }
    }

    private fun navigateToImageViewerForUser(
        userImageUrl: String,
        userName: String,
    ) {
        setUiEvent(
            PlanDetailUiEvent.NavigateToImageViewerForUser(
                image = userImageUrl,
                userName = userName,
            ),
        )
    }

    private fun navigateToCommentDetail(comment: Comment) {
        uiState.checkState<PlanDetailUiState.Success> {
            setUiState(
                copy(
                    selectedUpdateComment = null,
                    selectedComment = null,
                ),
            )

            setUiEvent(
                PlanDetailUiEvent.NavigateToCommentDetail(
                    meetId = planItem.meetingId,
                    postId = planItem.commentCheckId,
                    comment = comment,
                ),
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(planDetailRoute: DetailRoute.PlanDetail): PlanDetailViewModel
    }

    companion object {
        private const val KEY_COMMENT_CHECK_ID = "commentCheckId"
        private const val KEY_MEET_ID = "meetId"
    }
}

sealed interface PlanDetailUiState : UiState {
    data object Loading : PlanDetailUiState

    data class Success(
        val user: User,
        val planItem: PlanItem,
        val commentState: TextFieldState = TextFieldState(),
        val comments: Flow<PagingData<CommentUiModel>>? = null,
        val meetingParticipants: List<User> = emptyList(),
        val searchMentions: List<User> = emptyList(),
        val selectedMentions: List<User> = emptyList(),
        val selectedImageIndex: Int = 0,
        val selectedComment: Comment? = null,
        val selectedUpdateComment: Comment? = null,
        val isShowApplyButton: Boolean = false,
        val isShowApplyCancelDialog: Boolean = false,
        val isShowPlanEditDialog: Boolean = false,
        val isShowPlanReportDialog: Boolean = false,
        val isShowCommentEditDialog: Boolean = false,
        val isShowCommentReportDialog: Boolean = false,
        val isShowMentionDialog: Boolean = false,
    ) : PlanDetailUiState

    data object NotFoundError : PlanDetailUiState

    data object CommonError : PlanDetailUiState
}

sealed interface PlanDetailUiAction : UiAction {
    data object OnClickBack : PlanDetailUiAction

    data object OnClickRefresh : PlanDetailUiAction

    data object OnClickParticipants : PlanDetailUiAction

    data object OnClickPlanDelete : PlanDetailUiAction

    data object OnClickPlanUpdate : PlanDetailUiAction

    data object OnClickPlanReport : PlanDetailUiAction

    data object OnClickMapDetail : PlanDetailUiAction

    data class OnClickPlanApply(
        val isApply: Boolean,
    ) : PlanDetailUiAction

    data class OnClickCommentLike(
        val comment: Comment,
    ) : PlanDetailUiAction

    data class OnClickCommentAddReply(
        val comment: Comment,
    ) : PlanDetailUiAction

    data class OnClickCommentReport(
        val comment: Comment,
    ) : PlanDetailUiAction

    data class OnClickCommentUpdate(
        val comment: Comment,
    ) : PlanDetailUiAction

    data class OnClickCommentDelete(
        val comment: Comment,
    ) : PlanDetailUiAction

    data class OnClickCommentUpload(
        val updateComment: Comment?,
    ) : PlanDetailUiAction

    data class OnClickCommentWebLink(
        val webLink: String,
    ) : PlanDetailUiAction

    data class OnClickReviewImage(
        val selectedImageIndex: Int,
    ) : PlanDetailUiAction

    data class OnClickUserProfileImage(
        val imageUrl: String,
        val userName: String,
    ) : PlanDetailUiAction

    data class OnClickMentionUser(
        val user: User,
    ) : PlanDetailUiAction

    data class OnShowMentionDialog(
        val keyword: String?,
    ) : PlanDetailUiAction

    data class OnShowPlanApplyCancelDialog(
        val isShow: Boolean,
    ) : PlanDetailUiAction

    data class OnShowPlanEditDialog(
        val isShow: Boolean,
    ) : PlanDetailUiAction

    data class OnShowPlanReportDialog(
        val isShow: Boolean,
    ) : PlanDetailUiAction

    data class OnShowCommentEditDialog(
        val isShow: Boolean,
        val comment: Comment?,
    ) : PlanDetailUiAction

    data class OnShowCommentReportDialog(
        val isShow: Boolean,
        val comment: Comment?,
    ) : PlanDetailUiAction
}

sealed interface PlanDetailUiEvent : UiEvent {
    data object NavigateToBack : PlanDetailUiEvent

    data class NavigateToParticipants(
        val viewIdType: ViewIdType,
    ) : PlanDetailUiEvent

    data class NavigateToPlanWrite(
        val planItem: PlanItem,
    ) : PlanDetailUiEvent

    data class NavigateToReviewWrite(
        val postId: String,
    ) : PlanDetailUiEvent

    data class NavigateToMapDetail(
        val placeName: String,
        val address: String,
        val latitude: Double,
        val longitude: Double,
    ) : PlanDetailUiEvent

    data class NavigateToImageViewerForReview(
        val images: List<String>,
        val position: Int,
    ) : PlanDetailUiEvent

    data class NavigateToImageViewerForUser(
        val image: String,
        val userName: String,
    ) : PlanDetailUiEvent

    data class NavigateToWebBrowser(
        val webLink: String,
    ) : PlanDetailUiEvent

    data class NavigateToCommentDetail(
        val meetId: String,
        val postId: String,
        val comment: Comment,
    ) : PlanDetailUiEvent

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : PlanDetailUiEvent
}
