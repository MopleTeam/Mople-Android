package com.moim.core.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.moim.core.common.di.IoDispatcher
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.model.Plan
import com.moim.core.model.Review
import com.moim.core.model.item.PlanItem
import com.moim.core.model.item.asPlanItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import java.time.ZonedDateTime
import javax.inject.Inject

class GetPlanItemsUseCase @Inject constructor(
    private val planRepository: PlanRepository,
    private val reviewRepository: ReviewRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    var loadedAt: ZonedDateTime = ZonedDateTime.now()

    operator fun invoke(params: Params) = Pager(
        config = PagingConfig(pageSize = params.size)
    ) {
        object : PagingSource<String, PlanItem>() {

            init {
                loadedAt = ZonedDateTime.now()
            }

            override fun getRefreshKey(state: PagingState<String, PlanItem>) = null

            override suspend fun load(loadParams: LoadParams<String>): LoadResult<String, PlanItem> {
                val page = loadParams.key ?: ""

                if (params.isPlanAtBefore) {
                    val planContainer = planRepository.getPlans(
                        meetingId = params.meetId,
                        cursor = page,
                        size = params.size
                    )
                    val nextCursor = planContainer.cursorPage.nextCursor

                    return try {
                        LoadResult.Page(
                            data = planContainer.content.map(Plan::asPlanItem),
                            prevKey = null,
                            nextKey = if (planContainer.cursorPage.isNext && planContainer.cursorPage.size >= params.size) nextCursor else null
                        )
                    } catch (e: Exception) {
                        LoadResult.Error(e)
                    }
                } else {
                    val reviewContainer = reviewRepository.getReviews(
                        meetingId = params.meetId,
                        cursor = page,
                        size = params.size
                    )
                    val nextCursor = reviewContainer.cursorPage.nextCursor

                    return try {
                        LoadResult.Page(
                            data = reviewContainer.content.map(Review::asPlanItem),
                            prevKey = null,
                            nextKey = if (reviewContainer.cursorPage.isNext && reviewContainer.cursorPage.size >= params.size) nextCursor else null
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