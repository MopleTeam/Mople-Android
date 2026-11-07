package com.moim.feature.meetingdetail

import androidx.lifecycle.viewModelScope
import com.moim.core.common.model.Notice
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.Plan
import com.moim.core.common.model.Review
import com.moim.core.common.model.item.PlanItem
import com.moim.core.common.model.item.asPlanItem
import com.moim.core.common.result.Result
import com.moim.core.common.result.data
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.notice.NoticeRepository
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.MeetingAction
import com.moim.core.ui.eventbus.NoticeAction
import com.moim.core.ui.eventbus.PlanAction
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.util.isActiveCheck
import com.moim.core.ui.view.PagingHelper
import com.moim.core.ui.view.ToastMessage
import com.moim.feature.meetingdetail.model.MeetingDetailIntent
import com.moim.feature.meetingdetail.model.MeetingDetailSideEffect
import com.moim.feature.meetingdetail.model.MeetingDetailState
import com.moim.feature.meetingdetail.model.toUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.syntax.Syntax
import java.io.IOException
import java.time.ZonedDateTime

@HiltViewModel(assistedFactory = MeetingDetailViewModel.Factory::class)
class MeetingDetailViewModel @AssistedInject constructor(
    private val planRepository: PlanRepository,
    private val reviewRepository: ReviewRepository,
    private val meetingRepository: MeetingRepository,
    private val userRepository: UserRepository,
    private val noticeRepository: NoticeRepository,
    private val planEventBus: EventBus<PlanAction>,
    meetingEventBus: EventBus<MeetingAction>,
    noticeEventBus: EventBus<NoticeAction>,
    @Assisted val meetingDetailRoute: DetailRoute.MeetingDetail,
) : MVIViewModel<MeetingDetailState, MeetingDetailSideEffect>(MeetingDetailState()) {
    private val meetingId = meetingDetailRoute.meetingId
    private var plansPagingJob: Job? = null
    private var reviewsPagingJob: Job? = null

    init {
        meetingEventBus.action
            .onEach { action ->
                intent {
                    when (action) {
                        is MeetingAction.MeetingUpdate -> {
                            if (!state.isSuccess) return@intent
                            reduce { state.copy(meeting = Result.Success(action.meeting)) }
                        }

                        is MeetingAction.MeetingInvalidate -> {
                            loadInitial()
                        }

                        else -> {
                            return@intent
                        }
                    }
                }
            }.launchIn(viewModelScope)

        planEventBus.action
            .onEach { action ->
                intent {
                    if (!state.isSuccess) return@intent

                    when (action) {
                        is PlanAction.PlanCreate -> {
                            applyPlanCreate(action.planItem)
                        }

                        is PlanAction.PlanUpdate -> {
                            applyPlanUpdate(action.planItem)
                        }

                        is PlanAction.PlanDelete -> {
                            applyPlanDelete(action.postId)
                        }

                        is PlanAction.PlanInvalidate -> {
                            getPlans()
                            getReviews()
                        }

                        is PlanAction.None -> {
                            return@intent
                        }
                    }
                }
            }.launchIn(viewModelScope)

        noticeEventBus.action
            .onEach { action ->
                intent {
                    if (!state.isSuccess) return@intent

                    when (action) {
                        is NoticeAction.NoticeCreate -> {
                            applyNoticeCreate(action.notice)
                        }

                        is NoticeAction.NoticeUpdate -> {
                            applyNoticeUpdate(action.notice)
                        }

                        is NoticeAction.NoticeDelete -> {
                            applyNoticeDelete(action.noticeId)
                        }

                        is NoticeAction.None -> {
                            return@intent
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    override suspend fun Syntax<MeetingDetailState, MeetingDetailSideEffect>.onContainerCreate() {
        loadInitial()
    }

    override fun onIntent(intent: Intent) {
        if (intent !is MeetingDetailIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is MeetingDetailIntent.BackClick -> {
                    postSideEffect(MeetingDetailSideEffect.NavigateToBack)
                }

                is MeetingDetailIntent.RefreshClick -> {
                    loadInitial()
                }

                is MeetingDetailIntent.PlanWriteClick -> {
                    navigateToPlanWrite()
                }

                is MeetingDetailIntent.MeetingSettingClick -> {
                    navigateToMeetingSetting()
                }

                is MeetingDetailIntent.MeetingNoticeClick -> {
                    postSideEffect(MeetingDetailSideEffect.NavigateToMeetingNotice(meetingId))
                }

                is MeetingDetailIntent.MeetingNoticeDetailClick -> {
                    postSideEffect(
                        MeetingDetailSideEffect.NavigateToMeetingNoticeDetail(
                            meetId = meetingId,
                            noticeId = intent.noticeId,
                        ),
                    )
                }

                is MeetingDetailIntent.MeetingInviteClick -> {
                    getInviteLink()
                }

                is MeetingDetailIntent.PlanTabClick -> {
                    if (state.isPlanSelected == intent.isBefore) return@intent
                    reduce { state.copy(isPlanSelected = intent.isBefore) }
                }

                is MeetingDetailIntent.PlanApplyClick -> {
                    setPlanApply(intent.planItem, intent.isApply)
                }

                is MeetingDetailIntent.PlanDetailClick -> {
                    postSideEffect(MeetingDetailSideEffect.NavigateToPlanDetail(intent.viewIdType))
                }

                is MeetingDetailIntent.MeetingImageClick -> {
                    postSideEffect(
                        MeetingDetailSideEffect.NavigateToImageViewer(intent.imageUrl, intent.meetingName),
                    )
                }

                is MeetingDetailIntent.NextPageLoad -> {
                    if (state.isPlanSelected) {
                        getPlans(state.plansPagingInfo.nextCursor)
                    } else {
                        getReviews(state.reviewsPagingInfo.nextCursor)
                    }
                }

                is MeetingDetailIntent.PlanApplyCancelDialogShow -> {
                    reduce {
                        state.copy(
                            isShowApplyCancelDialog = intent.isShow,
                            cancelPlanItem = intent.cancelPlanItem,
                        )
                    }
                }
            }
        }
    }

    private fun loadInitial() {
        intent {
            reduce { state.copy(meeting = Result.Loading) }

            try {
                val (user, meeting) =
                    coroutineScope {
                        val userDeferred = async { userRepository.getUser().first() }
                        val meetingDeferred = async { meetingRepository.getMeeting(meetingId) }
                        userDeferred.await() to meetingDeferred.await()
                    }

                reduce {
                    state.copy(
                        userId = user.userId,
                        meeting = Result.Success(meeting),
                    )
                }

                getCurrentNotice()
                getPlans()
                getReviews()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                reduce { state.copy(meeting = Result.Error(e)) }
            }
        }
    }

    private fun getCurrentNotice() {
        intent {
            val notice =
                runCatching {
                    noticeRepository
                        .getNotices(
                            meetId = meetingId,
                            cursor = "",
                            size = 1,
                            filterType = null,
                        ).content
                        .firstOrNull()
                }.getOrNull()

            reduce { state.copy(notice = notice?.toUiModel()) }
        }
    }

    private fun getPlans(cursor: String? = null) {
        if (plansPagingJob.isActiveCheck()) return
        plansPagingJob =
            intent {
                handlePlansPagingData(
                    pagingData = null,
                    isLoading = true,
                    cursor = cursor,
                )

                val pagingData =
                    runCatching {
                        planRepository.getPlans(
                            meetingId = meetingId,
                            cursor = cursor ?: "",
                            size = 30,
                        )
                    }.getOrNull()

                handlePlansPagingData(
                    pagingData = pagingData,
                    isLoading = false,
                    cursor = cursor,
                )
            }
    }

    private fun getReviews(cursor: String? = null) {
        if (reviewsPagingJob.isActiveCheck()) return
        reviewsPagingJob =
            intent {
                handleReviewsPagingData(
                    pagingData = null,
                    isLoading = true,
                    cursor = cursor,
                )

                val pagingData =
                    runCatching {
                        reviewRepository.getReviews(
                            meetingId = meetingId,
                            cursor = cursor ?: "",
                            size = 30,
                        )
                    }.getOrNull()

                handleReviewsPagingData(
                    pagingData = pagingData,
                    isLoading = false,
                    cursor = cursor,
                )
            }
    }

    private suspend fun Syntax<MeetingDetailState, MeetingDetailSideEffect>.handlePlansPagingData(
        pagingData: PaginationContainer<List<Plan>>?,
        isLoading: Boolean,
        cursor: String?,
    ) {
        val result =
            PagingHelper.handlePagingResult(
                pagingData = pagingData,
                isLoading = isLoading,
                currentPagingInfo = state.plansPagingInfo,
                currentItems = state.plans,
                isInitialLoad = cursor == null,
                transform = { items -> items.map(Plan::asPlanItem) },
            )

        reduce {
            state.copy(
                plansPagingInfo = result.pagingInfo,
                plans = result.items,
                planTotalCount = result.pagingInfo.totalCount,
            )
        }
    }

    private suspend fun Syntax<MeetingDetailState, MeetingDetailSideEffect>.handleReviewsPagingData(
        pagingData: PaginationContainer<List<Review>>?,
        isLoading: Boolean,
        cursor: String?,
    ) {
        val result =
            PagingHelper.handlePagingResult(
                pagingData = pagingData,
                isLoading = isLoading,
                currentPagingInfo = state.reviewsPagingInfo,
                currentItems = state.reviews,
                isInitialLoad = cursor == null,
                transform = { items -> items.map(Review::asPlanItem) },
            )

        reduce {
            state.copy(
                reviewsPagingInfo = result.pagingInfo,
                reviews = result.items,
                reviewTotalCount = result.pagingInfo.totalCount,
            )
        }
    }

    private suspend fun Syntax<MeetingDetailState, MeetingDetailSideEffect>.applyPlanCreate(newItem: PlanItem) {
        if (newItem.meetingId != meetingId || newItem.isPlanAtBefore.not()) return

        reduce {
            state.copy(
                plans = listOf(newItem) + state.plans,
                planTotalCount = state.planTotalCount + 1,
            )
        }
    }

    private suspend fun Syntax<MeetingDetailState, MeetingDetailSideEffect>.applyPlanUpdate(newItem: PlanItem) {
        if (newItem.meetingId != meetingId) return

        if (newItem.isPlanAtBefore) {
            val updated = state.plans.map { if (it.postId == newItem.postId) newItem else it }
            reduce { state.copy(plans = updated) }
        } else {
            val updated = state.reviews.map { if (it.postId == newItem.postId) newItem else it }
            reduce { state.copy(reviews = updated) }
        }
    }

    private suspend fun Syntax<MeetingDetailState, MeetingDetailSideEffect>.applyPlanDelete(postId: String) {
        reduce {
            val newPlans = state.plans.filterNot { it.postId == postId }
            val newReviews = state.reviews.filterNot { it.postId == postId }
            val isPlanRemoved = newPlans.size != state.plans.size
            val isReviewRemoved = newReviews.size != state.reviews.size

            state.copy(
                plans = newPlans,
                reviews = newReviews,
                planTotalCount = if (isPlanRemoved) (state.planTotalCount - 1).coerceAtLeast(0) else state.planTotalCount,
                reviewTotalCount = if (isReviewRemoved) (state.reviewTotalCount - 1).coerceAtLeast(0) else state.reviewTotalCount,
            )
        }
    }

    private suspend fun Syntax<MeetingDetailState, MeetingDetailSideEffect>.applyNoticeCreate(notice: Notice) {
        if (notice.meetId != meetingId) return

        reduce { state.copy(notice = notice.toUiModel()) }
    }

    private suspend fun Syntax<MeetingDetailState, MeetingDetailSideEffect>.applyNoticeUpdate(notice: Notice) {
        if (state.notice?.noticeId != notice.noticeId) return

        reduce { state.copy(notice = notice.toUiModel()) }
    }

    private fun Syntax<MeetingDetailState, MeetingDetailSideEffect>.applyNoticeDelete(noticeId: String) {
        if (state.notice?.noticeId != noticeId) return

        getCurrentNotice()
    }

    private suspend fun Syntax<MeetingDetailState, MeetingDetailSideEffect>.setPlanApply(
        planItem: PlanItem,
        isApply: Boolean,
    ) {
        setLoading(true)

        try {
            if (isApply) {
                planRepository.joinPlan(planItem.postId)
            } else {
                planRepository.leavePlan(planItem.postId)
            }

            planEventBus.send(PlanAction.PlanUpdate(planItem = planItem.copy(isParticipant = !planItem.isParticipant)))

            if (isApply.not()) {
                reduce { state.copy(cancelPlanItem = null, isShowApplyCancelDialog = false) }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<MeetingDetailState, MeetingDetailSideEffect>.getInviteLink() {
        setLoading(true)

        try {
            val inviteCode = meetingRepository.getMeetingInviteCode(meetingId)
            postSideEffect(MeetingDetailSideEffect.NavigateToExternalShareUrl(inviteCode))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<MeetingDetailState, MeetingDetailSideEffect>.navigateToPlanWrite() {
        val meeting = state.meeting.data ?: return

        postSideEffect(
            MeetingDetailSideEffect.NavigateToPlanWrite(
                Plan(
                    meetingId = meeting.id,
                    meetingName = meeting.name,
                    meetingImageUrl = meeting.imageUrl,
                    planAt = ZonedDateTime.now(),
                ),
            ),
        )
    }

    private suspend fun Syntax<MeetingDetailState, MeetingDetailSideEffect>.navigateToMeetingSetting() {
        val meeting = state.meeting.data ?: return

        postSideEffect(MeetingDetailSideEffect.NavigateToMeetingSetting(meeting))
    }

    private suspend fun Syntax<MeetingDetailState, MeetingDetailSideEffect>.showErrorToast(exception: Throwable) {
        val message = if (exception is IOException) ToastMessage.NetworkErrorMessage else ToastMessage.ServerErrorMessage
        postSideEffect(MeetingDetailSideEffect.ShowToastMessage(message))
    }

    @AssistedFactory
    interface Factory {
        fun create(meetingDetailRoute: DetailRoute.MeetingDetail): MeetingDetailViewModel
    }
}
