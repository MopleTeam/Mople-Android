package com.moim.feature.plandetail

import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.insert
import androidx.lifecycle.viewModelScope
import com.moim.core.common.exception.ForbiddenException
import com.moim.core.common.exception.NotFoundException
import com.moim.core.common.model.Comment
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.User
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.model.isChild
import com.moim.core.common.model.item.CommentUiModel
import com.moim.core.common.model.item.PlanItem
import com.moim.core.common.result.Result
import com.moim.core.common.result.data
import com.moim.core.crashreport.CrashReporter
import com.moim.core.data.datasource.comment.CommentRepository
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.domain.usecase.GetPlanItemUseCase
import com.moim.core.ui.eventbus.CommentAction
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.PlanAction
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
import com.moim.feature.plandetail.model.PlanDetailIntent
import com.moim.feature.plandetail.model.PlanDetailSideEffect
import com.moim.feature.plandetail.model.PlanDetailState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.syntax.Syntax
import java.io.IOException
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel(assistedFactory = PlanDetailViewModel.Factory::class)
class PlanDetailViewModel @AssistedInject constructor(
    private val userRepository: UserRepository,
    private val getPlanItemUseCase: GetPlanItemUseCase,
    private val planRepository: PlanRepository,
    private val meetingRepository: MeetingRepository,
    private val reviewRepository: ReviewRepository,
    private val commentRepository: CommentRepository,
    private val planEventBus: EventBus<PlanAction>,
    private val commentEventBus: EventBus<CommentAction>,
    private val crashReporter: CrashReporter,
    @Assisted val planDetailRoute: DetailRoute.PlanDetail,
) : MVIViewModel<PlanDetailState, PlanDetailSideEffect>(PlanDetailState()) {
    private val viewIdType = planDetailRoute.viewIdType

    private var searchJob: Job? = null
    private var commentsPagingJob: Job? = null

    init {
        planEventBus.action
            .onEach { action ->
                intent {
                    when (action) {
                        is PlanAction.PlanUpdate -> {
                            if (!state.isSuccess) return@intent
                            reduce { state.copy(planItem = Result.Success(action.planItem)) }
                        }

                        is PlanAction.PlanInvalidate -> {
                            loadPlanDetail()
                        }

                        else -> {
                            return@intent
                        }
                    }
                }
            }.launchIn(viewModelScope)

        commentEventBus.action
            .onEach { action ->
                intent {
                    if (!state.isSuccess) return@intent

                    when (action) {
                        is CommentAction.CommentCreate -> applyCommentCreate(action.commentUiModel)
                        is CommentAction.CommentUpdate -> applyCommentUpdate(action.commentUiModel)
                        is CommentAction.CommentDelete -> applyCommentDelete(action.commentId)
                        is CommentAction.None -> Unit
                    }
                }
            }.launchIn(viewModelScope)
    }

    override suspend fun Syntax<PlanDetailState, PlanDetailSideEffect>.onContainerCreate() {
        loadPlanDetail()
    }

    override fun onIntent(intent: Intent) {
        if (intent !is PlanDetailIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is PlanDetailIntent.BackClick -> {
                    postSideEffect(PlanDetailSideEffect.NavigateToBack)
                }

                is PlanDetailIntent.RefreshClick -> {
                    loadPlanDetail()
                }

                is PlanDetailIntent.ParticipantsClick -> {
                    navigateToParticipants()
                }

                is PlanDetailIntent.PlanUpdateClick -> {
                    navigateToPlanWrite()
                }

                is PlanDetailIntent.PlanDeleteClick -> {
                    deletePlan()
                }

                is PlanDetailIntent.PlanReportClick -> {
                    reportPlan()
                }

                is PlanDetailIntent.PlanApplyClick -> {
                    planApply(intent.isApply)
                }

                is PlanDetailIntent.MapDetailClick -> {
                    navigateToMapDetail()
                }

                is PlanDetailIntent.CommentLikeClick -> {
                    setLikeComment(intent.comment)
                }

                is PlanDetailIntent.CommentAddReplyClick -> {
                    navigateToCommentDetail(intent.comment)
                }

                is PlanDetailIntent.CommentUploadClick -> {
                    uploadComment(intent.updateComment)
                }

                is PlanDetailIntent.CommentReportClick -> {
                    reportComment(intent.comment)
                }

                is PlanDetailIntent.CommentUpdateClick -> {
                    updateComment(intent.comment)
                }

                is PlanDetailIntent.CommentDeleteClick -> {
                    deleteComment(intent.comment)
                }

                is PlanDetailIntent.CommentWebLinkClick -> {
                    postSideEffect(PlanDetailSideEffect.NavigateToWebBrowser(intent.webLink))
                }

                is PlanDetailIntent.ReviewImageClick -> {
                    navigateToImageViewerForReview(intent.selectedImageIndex)
                }

                is PlanDetailIntent.UserProfileImageClick -> {
                    postSideEffect(
                        PlanDetailSideEffect.NavigateToImageViewerForUser(
                            image = intent.imageUrl,
                            userName = intent.userName,
                        ),
                    )
                }

                is PlanDetailIntent.MentionUserClick -> {
                    setSelectedUser(intent.user)
                }

                is PlanDetailIntent.NextCommentsPageLoad -> {
                    getComments(state.commentsPagingInfo.nextCursor)
                }

                is PlanDetailIntent.MentionDialogShow -> {
                    showMentionDialog(intent.keyword)
                }

                is PlanDetailIntent.PlanApplyCancelDialogShow -> {
                    reduce { state.copy(isShowApplyCancelDialog = intent.isShow) }
                }

                is PlanDetailIntent.PlanEditDialogShow -> {
                    reduce { state.copy(isShowPlanEditDialog = intent.isShow) }
                }

                is PlanDetailIntent.PlanReportDialogShow -> {
                    reduce { state.copy(isShowPlanReportDialog = intent.isShow) }
                }

                is PlanDetailIntent.CommentEditDialogShow -> {
                    reduce {
                        state.copy(
                            isShowCommentEditDialog = intent.isShow,
                            selectedComment = intent.comment,
                        )
                    }
                }

                is PlanDetailIntent.CommentReportDialogShow -> {
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

    private fun loadPlanDetail() {
        intent {
            reduce { state.copy(planItem = Result.Loading, isNotFoundError = false) }

            try {
                val (user, post) =
                    coroutineScope {
                        val userDeferred = async { userRepository.getUser().first() }
                        val postDeferred = async { getPlanItemUseCase(GetPlanItemUseCase.Params(viewIdType)).first() }
                        userDeferred.await() to postDeferred.await()
                    }

                reduce {
                    state.copy(
                        user = user,
                        planItem = Result.Success(post),
                        isShowApplyButton = post.planAt.isAfter(ZonedDateTime.now()) && user.userId != post.userId,
                    )
                }

                getComments()
                getMeetingParticipants(post.meetingId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val isNotFoundError = e is ForbiddenException || e is NotFoundException
                if (!isNotFoundError) crashReporter.logException(e)

                reduce { state.copy(planItem = Result.Error(e), isNotFoundError = isNotFoundError) }
            }
        }
    }

    private fun getMeetingParticipants(meetingId: String) {
        intent {
            val participants =
                runCatching {
                    meetingRepository
                        .getMeetingParticipants(
                            meetingId = meetingId,
                            cursor = "",
                            size = 100,
                        ).content
                }.getOrNull() ?: return@intent

            reduce { state.copy(meetingParticipants = participants) }
        }
    }

    private fun getComments(cursor: String? = null) {
        if (commentsPagingJob.isActiveCheck()) return
        commentsPagingJob =
            intent {
                val postId = state.planItem.data?.commentCheckId ?: return@intent

                handleCommentsPagingData(
                    pagingData = null,
                    isLoading = true,
                    cursor = cursor,
                )

                val pagingData =
                    runCatching {
                        commentRepository.getComments(
                            postId = postId,
                            cursor = cursor ?: "",
                            size = 30,
                        )
                    }.getOrNull()

                handleCommentsPagingData(
                    pagingData = pagingData,
                    isLoading = false,
                    cursor = cursor,
                )
            }
    }

    private suspend fun Syntax<PlanDetailState, PlanDetailSideEffect>.handleCommentsPagingData(
        pagingData: PaginationContainer<List<Comment>>?,
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
                transform = { items -> items.map { it.createCommentUiModel() } },
            )

        reduce {
            state.copy(
                commentsPagingInfo = result.pagingInfo,
                comments = result.items,
            )
        }
    }

    private suspend fun Syntax<PlanDetailState, PlanDetailSideEffect>.applyCommentCreate(newItem: CommentUiModel) {
        if (newItem.comment.isChild()) return
        if (state.comments.any { it.comment.commentId == newItem.comment.commentId }) return

        reduce { state.copy(comments = listOf(newItem) + state.comments) }
    }

    private suspend fun Syntax<PlanDetailState, PlanDetailSideEffect>.applyCommentUpdate(newItem: CommentUiModel) {
        val updated =
            state.comments.map {
                if (it.comment.commentId == newItem.comment.commentId) newItem else it
            }

        reduce { state.copy(comments = updated) }
    }

    private suspend fun Syntax<PlanDetailState, PlanDetailSideEffect>.applyCommentDelete(commentId: String) {
        val filtered = state.comments.filterNot { it.comment.commentId == commentId }

        reduce { state.copy(comments = filtered) }
    }

    private suspend fun Syntax<PlanDetailState, PlanDetailSideEffect>.updateComment(comment: Comment) {
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

    private suspend fun Syntax<PlanDetailState, PlanDetailSideEffect>.planApply(isApply: Boolean) {
        val currentPlanItem = state.planItem.data ?: return

        setLoading(true)

        try {
            if (isApply) {
                planRepository.joinPlan(viewIdType.id)
            } else {
                planRepository.leavePlan(viewIdType.id)
            }

            val planItem = currentPlanItem.copy(isParticipant = isApply)
            planEventBus.send(PlanAction.PlanUpdate(planItem = planItem))

            reduce {
                state.copy(
                    planItem = Result.Success(planItem),
                    isShowApplyCancelDialog = false,
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

    private suspend fun Syntax<PlanDetailState, PlanDetailSideEffect>.reportPlan() {
        val planItem = state.planItem.data ?: return

        setLoading(true)

        try {
            if (planItem.isPlanAtBefore) {
                planRepository.reportPlan(planId = planItem.postId)
            } else {
                reviewRepository.reportReview(reviewId = planItem.postId)
            }

            postSideEffect(PlanDetailSideEffect.ShowToastMessage(ToastMessage.ReportCompletedMessage))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<PlanDetailState, PlanDetailSideEffect>.deletePlan() {
        val planItem = state.planItem.data ?: return

        setLoading(true)

        try {
            if (planItem.isPlanAtBefore) {
                planRepository.deletePlan(planId = planItem.postId)
            } else {
                reviewRepository.deleteReview(reviewId = planItem.postId)
            }

            planEventBus.send(PlanAction.PlanDelete(postId = planItem.postId))
            postSideEffect(PlanDetailSideEffect.NavigateToBack)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<PlanDetailState, PlanDetailSideEffect>.setLikeComment(updateComment: Comment) {
        setLoading(true)

        try {
            val newComment = commentRepository.updateLikeComment(updateComment.commentId)

            commentEventBus.send(CommentAction.CommentUpdate(commentUiModel = newComment.createCommentUiModel()))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<PlanDetailState, PlanDetailSideEffect>.uploadComment(updateComment: Comment?) {
        val currentPlanItem = state.planItem.data ?: return
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
                if (updateComment == null) {
                    commentRepository.createComment(
                        postId = currentPlanItem.commentCheckId,
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

            if (updateComment == null) {
                commentEventBus.send(CommentAction.CommentCreate(commentUiModel = newComment))

                reduce {
                    val planItem = requireNotNull(state.planItem.data)

                    state.copy(
                        planItem = Result.Success(planItem.copy(commentCount = planItem.commentCount.plus(1))),
                        comments = listOf(newComment) + state.comments,
                        selectedMentions = emptyList(),
                    )
                }
            } else {
                commentEventBus.send(CommentAction.CommentUpdate(commentUiModel = newComment))

                reduce {
                    val updated =
                        state.comments.map { uiModel ->
                            if (uiModel.comment.commentId == newComment.comment.commentId) newComment else uiModel
                        }

                    state.copy(
                        comments = updated,
                        selectedUpdateComment = null,
                        selectedMentions = emptyList(),
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

    private suspend fun Syntax<PlanDetailState, PlanDetailSideEffect>.deleteComment(comment: Comment) {
        setLoading(true)

        try {
            commentRepository.deleteComment(comment.commentId)

            commentEventBus.send(CommentAction.CommentDelete(commentId = comment.commentId))
            if (state.selectedUpdateComment != null) state.commentState.clearText()

            reduce {
                val planItem = requireNotNull(state.planItem.data)

                state.copy(
                    planItem = Result.Success(planItem.copy(commentCount = planItem.commentCount.minus(1))),
                    comments = state.comments.filterNot { it.comment.commentId == comment.commentId },
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

    private suspend fun Syntax<PlanDetailState, PlanDetailSideEffect>.reportComment(comment: Comment) {
        setLoading(true)

        try {
            commentRepository.reportComment(commentId = comment.commentId)

            postSideEffect(PlanDetailSideEffect.ShowToastMessage(ToastMessage.ReportCompletedMessage))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<PlanDetailState, PlanDetailSideEffect>.setSelectedUser(user: User) {
        val selectMentions =
            state.selectedMentions
                .toMutableList()
                .apply { add(user) }
                .distinct()
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
                delay(MENTION_SEARCH_DEBOUNCE_MILLIS.milliseconds)

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

    private suspend fun Syntax<PlanDetailState, PlanDetailSideEffect>.navigateToPlanWrite() {
        val planItem = state.planItem.data ?: return

        if (planItem.isPlanAtBefore) {
            postSideEffect(PlanDetailSideEffect.NavigateToPlanWrite(planItem))
        } else {
            postSideEffect(PlanDetailSideEffect.NavigateToReviewWrite(planItem.postId))
        }
    }

    private suspend fun Syntax<PlanDetailState, PlanDetailSideEffect>.navigateToMapDetail() {
        val planItem = state.planItem.data ?: return

        postSideEffect(
            PlanDetailSideEffect.NavigateToMapDetail(
                placeName = planItem.placeName,
                address = planItem.loadAddress,
                latitude = planItem.latitude,
                longitude = planItem.longitude,
            ),
        )
    }

    private suspend fun Syntax<PlanDetailState, PlanDetailSideEffect>.navigateToParticipants() {
        val planItem = state.planItem.data ?: return

        val participantsViewIdType =
            if (planItem.isPlanAtBefore) {
                ViewIdType.PlanId(planItem.postId)
            } else {
                ViewIdType.ReviewId(planItem.postId)
            }

        postSideEffect(PlanDetailSideEffect.NavigateToParticipants(participantsViewIdType))
    }

    private suspend fun Syntax<PlanDetailState, PlanDetailSideEffect>.navigateToImageViewerForReview(selectedImageIndex: Int) {
        val planItem = state.planItem.data ?: return

        postSideEffect(
            PlanDetailSideEffect.NavigateToImageViewerForReview(
                images = planItem.reviewImages.map { it.imageUrl },
                position = selectedImageIndex,
            ),
        )
    }

    private suspend fun Syntax<PlanDetailState, PlanDetailSideEffect>.navigateToCommentDetail(comment: Comment) {
        val planItem = state.planItem.data ?: return

        reduce {
            state.copy(
                selectedUpdateComment = null,
                selectedComment = null,
            )
        }

        postSideEffect(
            PlanDetailSideEffect.NavigateToCommentDetail(
                meetId = planItem.meetingId,
                postId = planItem.commentCheckId,
                comment = comment,
            ),
        )
    }

    private suspend fun Syntax<PlanDetailState, PlanDetailSideEffect>.showErrorToast(exception: Throwable) {
        val message = if (exception is IOException) ToastMessage.NetworkErrorMessage else ToastMessage.ServerErrorMessage
        postSideEffect(PlanDetailSideEffect.ShowToastMessage(message))
    }

    @AssistedFactory
    interface Factory {
        fun create(planDetailRoute: DetailRoute.PlanDetail): PlanDetailViewModel
    }

    companion object {
        private const val MENTION_SEARCH_DEBOUNCE_MILLIS = 400L
    }
}
