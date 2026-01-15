package com.moim.core.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.moim.core.common.di.IoDispatcher
import com.moim.core.common.model.Plan
import com.moim.core.common.model.Review
import com.moim.core.common.model.item.PlanItem
import com.moim.core.common.model.item.asPlanItem
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.review.ReviewRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import java.time.ZonedDateTime
import javax.inject.Inject

class GetPlanItemsUseCase @Inject constructor(
    private val planRepository: PlanRepository,
    private val reviewRepository: ReviewRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    var loadedAt: ZonedDateTime = ZonedDateTime.now()

    operator fun invoke(params: Params) =
        Pager(
            config = PagingConfig(pageSize = params.size),
        ) {
            object : PagingSource<String, PlanItem>() {
                init {
                    loadedAt = ZonedDateTime.now()
                }

                override fun getRefreshKey(state: PagingState<String, PlanItem>) = null

                override suspend fun load(loadParams: LoadParams<String>): LoadResult<String, PlanItem> {
                    val page = loadParams.key ?: ""

                    if (params.isPlanAtBefore) {
                        return try {
                            val planContainer =
                                planRepository.getPlans(
                                    meetingId = params.meetId,
                                    cursor = page,
                                    size = params.size,
                                )
                            val nextCursor = planContainer.page.nextCursor

                            LoadResult.Page(
                                data = planContainer.content.map(Plan::asPlanItem),
                                prevKey = null,
                                nextKey = if (planContainer.page.isNext && planContainer.page.size >= params.size) nextCursor else null,
                            )
                        } catch (e: Exception) {
                            LoadResult.Error(e)
                        }
                    } else {
                        return try {
                            val reviewContainer =
                                reviewRepository.getReviews(
                                    meetingId = params.meetId,
                                    cursor = page,
                                    size = params.size,
                                )
                            val nextCursor = reviewContainer.page.nextCursor

                            LoadResult.Page(
                                data = reviewContainer.content.map(Review::asPlanItem),
                                prevKey = null,
                                nextKey = if (reviewContainer.page.isNext && reviewContainer.page.size >= params.size) nextCursor else null,
                            )
                        } catch (e: Exception) {
                            LoadResult.Error(e)
                        }
                    }
                }
            }
        }.flow.flowOn(ioDispatcher)

    data class Params(
        val meetId: String,
        val isPlanAtBefore: Boolean,
        val size: Int = 30,
    )
}
