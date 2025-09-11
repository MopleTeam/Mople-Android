package com.moim.core.domain.usecase

import com.moim.core.common.di.IoDispatcher
import com.moim.core.common.model.item.asPlanItem
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.review.ReviewRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetPlanItemUseCase @Inject constructor(
    private val planRepository: PlanRepository,
    private val reviewRepository: ReviewRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    operator fun invoke(params: Params) = flow {
        if (params.isPlanAtBefore) {
            emit(planRepository.getPlan(params.id).first().asPlanItem())
        } else {
            emit(reviewRepository.getReview(params.id).first().asPlanItem())
        }
    }.flowOn(ioDispatcher)

    data class Params(
        val id: String,
        val isPlanAtBefore: Boolean
    )
}