package com.moim.feature.plandetail

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.common.view.checkState
import com.moim.core.data.datasource.comment.CommentRepository
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.designsystem.R
import com.moim.core.model.Comment
import com.moim.core.model.Plan
import com.moim.core.model.Review
import com.moim.core.model.User
import com.moim.feature.plandetail.model.PlanDetailUiModel
import com.moim.feature.plandetail.model.createDetailUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val planRepository: PlanRepository,
    private val reviewRepository: ReviewRepository,
    private val commentRepository: CommentRepository
) : BaseViewModel() {

    private val postId
        get() = savedStateHandle.get<String>(KEY_POST_ID) ?: ""

    private val isPlan
        get() = savedStateHandle.get<Boolean>(KEY_IS_PLAN) ?: false

    private val planId = savedStateHandle.getStateFlow<String?>(KEY_PLAN_ID, null)

    private val planResult = loadDataSignal
        .flatMapLatest {
            combine(
                userRepository.getUser(),
                (if (isPlan) planRepository.getPlan(postId) else reviewRepository.getReview(postId)),
                ::Pair
            ).asResult()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    private val commentResult = planId
        .filterNotNull()
        .flatMapLatest { commentRepository.getComments(it).asResult() }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            launch {
                planResult.collect { result ->
                    when (result) {
                        is Result.Loading -> setUiState(PlanDetailUiState.Loading)
                        is Result.Success -> {
                            val (user, post) = result.data

                            val uiState = when (post) {
                                is Plan -> PlanDetailUiState.Success(user, post.createDetailUiModel())
                                is Review -> PlanDetailUiState.Success(user, post.createDetailUiModel())
                                else -> PlanDetailUiState.Error
                            }

                            setUiState(uiState)

                            if (uiState is PlanDetailUiState.Success) {
                                savedStateHandle[KEY_PLAN_ID] = uiState.planDetail.postId
                            }
                        }

                        is Result.Error -> setUiState(PlanDetailUiState.Error)
                    }
                }
            }

            launch {
                commentResult.collect { result ->
                    uiState.checkState<PlanDetailUiState.Success> {
                        when (result) {
                            is Result.Loading -> return@collect
                            is Result.Success -> setUiState(copy(comments = result.data))
                            else -> setUiEvent(PlanDetailUiEvent.ShowToastMessage(R.string.plan_detail_comment_error))
                        }
                    }
                }
            }
        }
    }

    fun onUiAction(uiAction: PlanDetailUiAction) {
        when (uiAction) {
            is PlanDetailUiAction.OnClickBack -> setUiEvent(PlanDetailUiEvent.NavigateToBack)
            is PlanDetailUiAction.OnClickRefresh -> onRefresh()
            is PlanDetailUiAction.OnClickParticipants -> {}
            is PlanDetailUiAction.OnClickPlanDelete -> {}
            is PlanDetailUiAction.OnClickPlanUpdate -> {}
            is PlanDetailUiAction.OnClickPlanReport -> {}
            is PlanDetailUiAction.OnClickCommentUpload -> uploadComment(uiAction.commentText, uiAction.updateComment)
            is PlanDetailUiAction.OnClickCommentReport -> {}
            is PlanDetailUiAction.OnClickCommentUpdate -> {}
            is PlanDetailUiAction.OnClickCommentDelete -> {}
            is PlanDetailUiAction.OnShowReviewImageCropDialog -> showPlanDetailImageCropDialog(uiAction.isShow, uiAction.selectedImageIndex)
            is PlanDetailUiAction.OnShowPlanEditDialog -> showPlanEditDialog(uiAction.isShow)
            is PlanDetailUiAction.OnShowPlanReportDialog -> showPlanReportDialog(uiAction.isShow)
            is PlanDetailUiAction.OnShowCommentEditDialog -> showCommentEditDialog(uiAction.isShow, uiAction.comment)
            is PlanDetailUiAction.OnShowCommentReportDialog -> showCommentReportDialog(uiAction.isShow, uiAction.comment)
        }
    }

    private fun uploadComment(
        content: String,
        updateComment: Comment?
    ) {
        viewModelScope.launch {
            uiState.checkState<PlanDetailUiState.Success> {
                if (updateComment != null) {
                    commentRepository.updateComment(
                        commentId = updateComment.commentId,
                        content = content.trim()
                    )
                } else {
                    commentRepository.createComment(
                        postId = postId,
                        content = content.trim()
                    )
                }.asResult().onEach { setLoading(it is Result.Loading) }.collect { result ->
                    when (result) {
                        is Result.Loading -> return@collect
                        is Result.Success -> setUiState(copy(comments = result.data))
                        is Result.Error -> setUiEvent(PlanDetailUiEvent.ShowToastMessage(R.string.common_error_disconnection))
                    }
                }
            }
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

    private fun showPlanDetailImageCropDialog(isShow: Boolean, selectedImageIndex: Int) {
        uiState.checkState<PlanDetailUiState.Success> {
            setUiState(copy(isShowReviewImageCropDialog = isShow, selectedImageIndex = selectedImageIndex))
        }
    }

    companion object {
        private const val KEY_PLAN_ID = "planId"
        private const val KEY_POST_ID = "postId"
        private const val KEY_IS_PLAN = "isPlan"
    }
}

sealed interface PlanDetailUiState : UiState {
    data object Loading : PlanDetailUiState

    data class Success(
        val user: User,
        val planDetail: PlanDetailUiModel,
        val comments: List<Comment> = emptyList(),
        val selectedImageIndex: Int = 0,
        val selectedComment: Comment? = null,
        val isShowPlanEditDialog: Boolean = false,
        val isShowPlanReportDialog: Boolean = false,
        val isShowCommentEditDialog: Boolean = false,
        val isShowCommentReportDialog: Boolean = false,
        val isShowReviewImageCropDialog: Boolean = false,
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
    data class OnClickCommentReport(val comment: Comment) : PlanDetailUiAction
    data class OnClickCommentUpdate(val comment: Comment) : PlanDetailUiAction
    data class OnClickCommentDelete(val comment: Comment) : PlanDetailUiAction
    data class OnClickCommentUpload(val commentText: String, val updateComment: Comment?) : PlanDetailUiAction
    data class OnShowReviewImageCropDialog(val isShow: Boolean, val selectedImageIndex: Int) : PlanDetailUiAction
    data class OnShowPlanEditDialog(val isShow: Boolean) : PlanDetailUiAction
    data class OnShowPlanReportDialog(val isShow: Boolean) : PlanDetailUiAction
    data class OnShowCommentEditDialog(val isShow: Boolean, val comment: Comment?) : PlanDetailUiAction
    data class OnShowCommentReportDialog(val isShow: Boolean, val comment: Comment?) : PlanDetailUiAction
}

sealed interface PlanDetailUiEvent : UiEvent {
    data object NavigateToBack : PlanDetailUiEvent
    data class NavigateToParticipants(val postId: String) : PlanDetailUiEvent
    data class NavigateToPlanWrite(val plan: Plan) : PlanDetailUiEvent
    data class ShowToastMessage(@StringRes val messageRes: Int) : PlanDetailUiEvent
}