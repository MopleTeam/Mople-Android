package com.moim.feature.participantlist

import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.User
import com.moim.core.common.model.ViewIdType
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.util.isActiveCheck
import com.moim.core.ui.view.PagingHelper
import com.moim.core.ui.view.ToastMessage
import com.moim.feature.participantlist.model.ParticipantListIntent
import com.moim.feature.participantlist.model.ParticipantListSideEffect
import com.moim.feature.participantlist.model.ParticipantListState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import org.orbitmvi.orbit.syntax.Syntax
import java.io.IOException

@HiltViewModel(assistedFactory = ParticipantListViewModel.Factory::class)
class ParticipantListViewModel @AssistedInject constructor(
    private val meetingRepository: MeetingRepository,
    private val planRepository: PlanRepository,
    private val reviewRepository: ReviewRepository,
    @Assisted val participantListRoute: DetailRoute.ParticipantList,
) : MVIViewModel<ParticipantListState, ParticipantListSideEffect>(participantListRoute.asState()) {
    private var pagingJob: Job? = null
    private val viewIdType = participantListRoute.viewIdType

    override suspend fun Syntax<ParticipantListState, ParticipantListSideEffect>.onContainerCreate() {
        getParticipants()
    }

    override fun onIntent(intent: Intent) {
        if (intent !is ParticipantListIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is ParticipantListIntent.BackClick -> {
                    postSideEffect(ParticipantListSideEffect.NavigateToBack)
                }

                is ParticipantListIntent.RefreshClick,
                is ParticipantListIntent.NextPageLoad,
                -> {
                    getParticipants(state.pagingInfo.nextCursor)
                }

                is ParticipantListIntent.UserImageClick -> {
                    postSideEffect(
                        ParticipantListSideEffect.NavigateToImageViewer(intent.userImage, intent.userName),
                    )
                }

                is ParticipantListIntent.MeetingInviteClick -> {
                    getInviteLink()
                }
            }
        }
    }

    private fun getParticipants(cursor: String? = null) {
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
                        when (viewIdType) {
                            is ViewIdType.MeetId -> {
                                meetingRepository.getMeetingParticipants(
                                    meetingId = viewIdType.id,
                                    cursor = cursor ?: "",
                                    size = 30,
                                )
                            }

                            is ViewIdType.PlanId -> {
                                planRepository.getPlanParticipants(
                                    planId = viewIdType.id,
                                    cursor = cursor ?: "",
                                    size = 30,
                                )
                            }

                            is ViewIdType.ReviewId -> {
                                reviewRepository.getReviewParticipants(
                                    reviewId = viewIdType.id,
                                    cursor = cursor ?: "",
                                    size = 30,
                                )
                            }

                            else -> {
                                throw IllegalStateException("this ViewTypeId is not allowed")
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

    private suspend fun Syntax<ParticipantListState, ParticipantListSideEffect>.handlePagingData(
        pagingData: PaginationContainer<List<User>>?,
        isLoading: Boolean,
        cursor: String?,
    ) {
        val result =
            PagingHelper.handlePagingResult(
                pagingData = pagingData,
                isLoading = isLoading,
                currentPagingInfo = state.pagingInfo,
                currentItems = state.participants,
                isInitialLoad = cursor == null,
                transform = { users -> users },
            )

        reduce {
            state.copy(
                pagingInfo = result.pagingInfo,
                participants = result.items,
            )
        }
    }

    private suspend fun Syntax<ParticipantListState, ParticipantListSideEffect>.getInviteLink() {
        setLoading(true)

        try {
            val inviteCode = meetingRepository.getMeetingInviteCode(viewIdType.id)
            postSideEffect(ParticipantListSideEffect.NavigateToExternalShareUrl(inviteCode))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<ParticipantListState, ParticipantListSideEffect>.showErrorToast(exception: Throwable) {
        val message = if (exception is IOException) ToastMessage.NetworkErrorMessage else ToastMessage.ServerErrorMessage
        postSideEffect(ParticipantListSideEffect.ShowToastMessage(message))
    }

    @AssistedFactory
    interface Factory {
        fun create(participantListRoute: DetailRoute.ParticipantList): ParticipantListViewModel
    }
}

private fun DetailRoute.ParticipantList.asState() = ParticipantListState(isMeeting = viewIdType is ViewIdType.MeetId)
