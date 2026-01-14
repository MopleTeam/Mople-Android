package com.moim.core.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.moim.core.common.di.IoDispatcher
import com.moim.core.common.model.User
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.review.ReviewRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class GetParticipantsUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository,
    private val planRepository: PlanRepository,
    private val reviewRepository: ReviewRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    operator fun invoke(params: Params) =
        Pager(
            config = PagingConfig(pageSize = params.size),
        ) {
            object : PagingSource<String, User>() {
                override fun getRefreshKey(state: PagingState<String, User>): String? = null

                override suspend fun load(loadParams: LoadParams<String>): LoadResult<String, User> {
                    val page = loadParams.key ?: ""
                    return try {
                        val participantContainer =
                            when {
                                params.isMeeting -> {
                                    meetingRepository.getMeetingParticipants(
                                        meetingId = params.id,
                                        cursor = page,
                                        size = params.size,
                                    )
                                }

                                params.isPlan -> {
                                    planRepository.getPlanParticipants(
                                        planId = params.id,
                                        cursor = page,
                                        size = params.size,
                                    )
                                }

                                else -> {
                                    reviewRepository.getReviewParticipants(
                                        reviewId = params.id,
                                        cursor = page,
                                        size = params.size,
                                    )
                                }
                            }
                        val nextCursor = participantContainer.page.nextCursor

                        LoadResult.Page(
                            data = participantContainer.content,
                            prevKey = null,
                            nextKey =
                                if (participantContainer.page.isNext &&
                                    participantContainer.page.size >= params.size
                                ) {
                                    nextCursor
                                } else {
                                    null
                                },
                        )
                    } catch (e: Exception) {
                        Timber.e("[GetParticipantsUseCase] error $e")
                        LoadResult.Error(e)
                    }
                }
            }
        }.flow.flowOn(ioDispatcher)

    data class Params(
        val id: String,
        val isMeeting: Boolean,
        val isPlan: Boolean,
        val size: Int = 30,
    )
}
