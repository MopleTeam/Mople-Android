package com.moim.feature.plandetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertSeparators
import androidx.paging.map
import com.moim.core.common.delegate.PlanAction
import com.moim.core.common.delegate.PlanItemViewModelDelegate
import com.moim.core.common.delegate.planItemStateIn
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.util.cancelIfActive
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
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.domain.usecase.GetCommentsUseCase
import com.moim.core.domain.usecase.GetPlanItemUseCase
import com.moim.core.model.Comment
import com.moim.core.model.User
import com.moim.core.model.item.PlanItem
import com.moim.feature.plandetail.model.CommentUiModel
import com.moim.feature.plandetail.model.createCommentUiModel
import com.moim.feature.plandetail.util.PlanDetailCommentAction
import com.moim.feature.plandetail.util.PlanDetailCommentViewModelDelegate
import com.moim.feature.plandetail.util.planDetailCommentStateIn
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
import javax.inject.Inject

@HiltViewModel
class PlanDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    userRepository: UserRepository,
    getPlanItemUseCase: GetPlanItemUseCase,
    private val planRepository: PlanRepository,
    private val meetingRepository: MeetingRepository,
    private val reviewRepository: ReviewRepository,
    private val commentRepository: CommentRepository,
    private val getCommentsUseCase: GetCommentsUseCase,
    planItemViewModelDelegate: PlanItemViewModelDelegate,
    planDetailCommentViewModelDelegate: PlanDetailCommentViewModelDelegate,
) : BaseViewModel(),
    PlanItemViewModelDelegate by planItemViewModelDelegate,
    PlanDetailCommentViewModelDelegate by planDetailCommentViewModelDelegate {

    private val postId
        get() = savedStateHandle.get<String>(KEY_POST_ID) ?: ""

    private val isPlan
        get() = savedStateHandle.get<Boolean>(KEY_IS_PLAN) ?: false

    private val planId = savedStateHandle.getStateFlow<String?>(KEY_PLAN_ID, null)
    private val meetId = savedStateHandle.getStateFlow<String?>(KEY_MEET_ID, null)

    private var searchJob: Job? = null

    private val planItemReceiver = planItemAction.planItemStateIn(viewModelScope)
    private val commentActionReceiver = commentAction.planDetailCommentStateIn(viewModelScope)

    private var _comments = planId
        .filterNotNull()
        .flatMapLatest { planId -> getCommentsUseCase(GetCommentsUseCase.Params(planId)) }
        .mapLatest { it.map { comment -> comment.createCommentUiModel() } }
        .cachedIn(viewModelScope)

    private val comments = commentActionReceiver.flatMapLatest { receiver ->
        when (receiver) {
            is PlanDetailCommentAction.None -> _comments

            is PlanDetailCommentAction.CommentCreate -> {
                _comments.map { pagingData ->
                    pagingData.checkedActionedAtIsBeforeLoadedAt(
                        actionedAt = receiver.actionAt,
                        loadedAt = getCommentsUseCase.loadedAt
                    ) {
                        pagingData.insertSeparators { before: CommentUiModel?, after: CommentUiModel? ->
                            if (before == null) {
                                return@insertSeparators receiver.commentUiModel
                            } else {
                                null
                            }
                        }
                    }
                }
            }

            is PlanDetailCommentAction.CommentUpdate -> {
                _comments.map { pagingData ->
                    pagingData.checkedActionedAtIsBeforeLoadedAt(
                        actionedAt = receiver.actionAt,
                        loadedAt = getCommentsUseCase.loadedAt
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

            is PlanDetailCommentAction.CommentDelete -> {
                _comments.map { pagingData ->
                    pagingData.checkedActionedAtIsBeforeLoadedAt(
                        actionedAt = receiver.actionAt,
                        loadedAt = getCommentsUseCase.loadedAt
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

    private val meetingParticipants = meetId
        .filterNotNull()
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

    private val planDetailUiState =
        combine(
            userRepository.getUser(),
            getPlanItemUseCase(GetPlanItemUseCase.Params(postId, isPlan)),
            ::Pair
        ).mapLatest { (user, post) ->
            val isShowApplyButton = post.planAt.isAfter(ZonedDateTime.now()) && user.userId != post.userId
            PlanDetailUiState.Success(
                user = user,
                planItem = post,
                isShowApplyButton = isShowApplyButton
            )
        }.asResult().mapLatest { result ->
            when (result) {
                is Result.Loading -> PlanDetailUiState.Loading
                is Result.Success -> result.data
                is Result.Error -> PlanDetailUiState.Error
            }
        }.restartableStateIn(viewModelScope, SharingStarted.Lazily, PlanDetailUiState.Loading)

    init {
        viewModelScope.launch {
            launch {
                planDetailUiState.collect { uiState ->
                    setUiState(uiState)
                    if (uiState is PlanDetailUiState.Success) {
                        savedStateHandle[KEY_PLAN_ID] = uiState.planItem.commentCheckId
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

                        else -> return@collect
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
            is PlanDetailUiAction.OnClickCommentAddReply -> {}
            is PlanDetailUiAction.OnClickCommentUpload -> uploadComment(uiAction.commentText, uiAction.updateComment)
            is PlanDetailUiAction.OnClickCommentReport -> reportComment(uiAction.comment)
            is PlanDetailUiAction.OnClickCommentUpdate -> updateComment(uiAction.comment)
            is PlanDetailUiAction.OnClickCommentDelete -> deleteComment(uiAction.comment)
            is PlanDetailUiAction.OnClickCommentWebLink -> setUiEvent(PlanDetailUiEvent.NavigateToWebBrowser(uiAction.webLink))
            is PlanDetailUiAction.OnClickReviewImage -> navigateToImageViewerForReview(uiAction.selectedImageIndex)
            is PlanDetailUiAction.OnClickUserProfileImage -> navigateToImageViewerForUser(uiAction.imageUrl, uiAction.userName)
            is PlanDetailUiAction.OnClickMentionUser -> setSelectedUser(uiAction.user)
            is PlanDetailUiAction.OnChangeCommentContent -> setCommentContent(uiAction.content)
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
            setUiState(copy(selectedUpdateComment = comment))
        }
    }

    private fun planApply(isApply: Boolean) {
        viewModelScope.launch {
            uiState.checkState<PlanDetailUiState.Success> {
                if (isApply) {
                    planRepository.joinPlan(postId)
                } else {
                    planRepository.leavePlan(postId)
                }.asResult().onEach { setLoading(it is Result.Loading) }.collect { result ->
                    when (result) {
                        is Result.Loading -> return@collect

                        is Result.Success -> {
                            val planItem = planItem.copy(isParticipant = isApply)

                            updatePlanItem(ZonedDateTime.now(), planItem)
                            setUiState(
                                copy(
                                    planItem = planItem,
                                    isShowApplyCancelDialog = false
                                )
                            )
                        }

                        is Result.Error -> showErrorToast(result.exception)
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
                        is Result.Loading -> return@collect
                        is Result.Success -> {
                            deletePlanItem(ZonedDateTime.now(), planItem.postId)
                            setUiEvent(PlanDetailUiEvent.NavigateToBack)
                        }

                        is Result.Error -> showErrorToast(result.exception)
                    }
                }
            }
        }
    }

    private fun setLikeComment(
        updateComment: Comment
    ) {
        viewModelScope.launch {
            commentRepository
                .updateLikeComment(updateComment.commentId)
                .asResult()
                .onEach { setLoading(it is Result.Loading) }
                .collect { result ->
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> updatePlanDetailComment(commentUiModel = result.data.createCommentUiModel())
                        is Result.Error -> showErrorToast(result.exception)
                    }
                }
        }
    }

    private fun setCommentContent(content: String) {
        uiState.checkState<PlanDetailUiState.Success> {
            setUiState(copy(inputContent = content))
        }
    }

    private fun uploadComment(
        content: String,
        updateComment: Comment?
    ) {
        viewModelScope.launch {
            uiState.checkState<PlanDetailUiState.Success> {
                if (updateComment == null) {
                    commentRepository.createComment(
                        postId = planItem.commentCheckId,
                        content = content.trim()
                    )

                } else {
                    commentRepository.updateComment(
                        commentId = updateComment.commentId,
                        content = content.trim()
                    )
                }.asResult().onEach { setLoading(it is Result.Loading) }.collect { result ->
                    uiState.checkState<PlanDetailUiState.Success> {
                        when (result) {
                            is Result.Loading -> return@collect

                            is Result.Success -> {
                                if (updateComment == null) {
                                    createPlanDetailComment(commentUiModel = result.data.createCommentUiModel())
                                    setUiState(copy(planItem = planItem.copy(commentCount = planItem.commentCount.plus(1))))
                                } else {
                                    updatePlanDetailComment(commentUiModel = result.data.createCommentUiModel())
                                    setUiState(copy(selectedUpdateComment = null))
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
                    uiState.checkState<PlanDetailUiState.Success> {
                        when (result) {
                            is Result.Loading -> return@collect

                            is Result.Success -> {
                                deletePlanDetailComment(commentId = comment.commentId)
                                setUiState(copy(planItem = planItem.copy(commentCount = planItem.commentCount.minus(1))))
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
            setUiState(
                copy(
                    selectedMentions = selectedMentions.toMutableList().apply { add(user) }.distinct(),
                    searchMentions = emptyList(),
                    isShowMentionDialog = false
                )
            )
        }
    }

    private fun showMentionDialog(keyword: String?) {
        searchJob.cancelIfActive()
        searchJob = viewModelScope.launch {
            delay(400)
            uiState.checkState<PlanDetailUiState.Success> {
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

    private fun showCommentEditDialog(isShow: Boolean, comment: Comment?) {
        uiState.checkState<PlanDetailUiState.Success> {
            setUiState(copy(isShowCommentEditDialog = isShow, selectedComment = comment))
        }
    }

    private fun showCommentReportDialog(isShow: Boolean, comment: Comment?) {
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
                    longitude = planItem.longitude
                )
            )
        }
    }

    private fun navigateToParticipants() {
        uiState.checkState<PlanDetailUiState.Success> {
            setUiEvent(PlanDetailUiEvent.NavigateToParticipants(planItem.postId, planItem.isPlanAtBefore))
        }
    }

    private fun navigateToImageViewerForReview(selectedImageIndex: Int) {
        uiState.checkState<PlanDetailUiState.Success> {
            setUiEvent(
                PlanDetailUiEvent.NavigateToImageViewerForReview(
                    images = this.planItem.reviewImages.map { it.imageUrl },
                    position = selectedImageIndex
                )
            )
        }
    }

    private fun navigateToImageViewerForUser(userImageUrl: String, userName: String) {
        setUiEvent(
            PlanDetailUiEvent.NavigateToImageViewerForUser(
                image = userImageUrl,
                userName = userName
            )
        )
    }

    companion object {
        private const val KEY_POST_ID = "postId"
        private const val KEY_PLAN_ID = "planId"
        private const val KEY_MEET_ID = "meetId"
        private const val KEY_IS_PLAN = "isPlan"
    }
}

sealed interface PlanDetailUiState : UiState {
    data object Loading : PlanDetailUiState

    data class Success(
        val user: User,
        val planItem: PlanItem,
        val inputContent: String = "",
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

    data object Error : PlanDetailUiState
}

sealed interface PlanDetailUiAction : UiAction {
    data object OnClickBack : PlanDetailUiAction
    data object OnClickRefresh : PlanDetailUiAction
    data object OnClickParticipants : PlanDetailUiAction
    data object OnClickPlanDelete : PlanDetailUiAction
    data object OnClickPlanUpdate : PlanDetailUiAction
    data object OnClickPlanReport : PlanDetailUiAction
    data object OnClickMapDetail : PlanDetailUiAction
    data class OnClickPlanApply(val isApply: Boolean) : PlanDetailUiAction
    data class OnClickCommentLike(val comment: Comment) : PlanDetailUiAction
    data class OnClickCommentAddReply(val comment: Comment) : PlanDetailUiAction
    data class OnClickCommentReport(val comment: Comment) : PlanDetailUiAction
    data class OnClickCommentUpdate(val comment: Comment) : PlanDetailUiAction
    data class OnClickCommentDelete(val comment: Comment) : PlanDetailUiAction
    data class OnClickCommentUpload(val commentText: String, val updateComment: Comment?) : PlanDetailUiAction
    data class OnClickCommentWebLink(val webLink: String) : PlanDetailUiAction
    data class OnClickReviewImage(val selectedImageIndex: Int) : PlanDetailUiAction
    data class OnClickUserProfileImage(val imageUrl: String, val userName: String) : PlanDetailUiAction
    data class OnClickMentionUser(val user: User) : PlanDetailUiAction
    data class OnChangeCommentContent(val content: String): PlanDetailUiAction
    data class OnShowMentionDialog(val keyword: String?) : PlanDetailUiAction
    data class OnShowPlanApplyCancelDialog(val isShow: Boolean) : PlanDetailUiAction
    data class OnShowPlanEditDialog(val isShow: Boolean) : PlanDetailUiAction
    data class OnShowPlanReportDialog(val isShow: Boolean) : PlanDetailUiAction
    data class OnShowCommentEditDialog(val isShow: Boolean, val comment: Comment?) : PlanDetailUiAction
    data class OnShowCommentReportDialog(val isShow: Boolean, val comment: Comment?) : PlanDetailUiAction
}

sealed interface PlanDetailUiEvent : UiEvent {
    data object NavigateToBack : PlanDetailUiEvent

    data class NavigateToParticipants(
        val postId: String,
        val isPlan: Boolean
    ) : PlanDetailUiEvent

    data class NavigateToPlanWrite(
        val planItem: PlanItem
    ) : PlanDetailUiEvent

    data class NavigateToReviewWrite(
        val postId: String
    ) : PlanDetailUiEvent

    data class NavigateToMapDetail(
        val placeName: String,
        val address: String,
        val latitude: Double,
        val longitude: Double
    ) : PlanDetailUiEvent

    data class NavigateToImageViewerForReview(
        val images: List<String>,
        val position: Int
    ) : PlanDetailUiEvent

    data class NavigateToImageViewerForUser(
        val image: String,
        val userName: String
    ) : PlanDetailUiEvent

    data class NavigateToWebBrowser(
        val webLink: String
    ) : PlanDetailUiEvent

    data class ShowToastMessage(
        val message: ToastMessage
    ) : PlanDetailUiEvent
}