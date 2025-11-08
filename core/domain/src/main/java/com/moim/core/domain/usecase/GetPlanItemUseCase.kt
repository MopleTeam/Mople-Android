package com.moim.core.domain.usecase

import com.moim.core.common.di.IoDispatcher
import com.moim.core.common.model.ViewIdType
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
        when (val type = params.viewIdType) {
            is ViewIdType.PlanId -> {
                emit(planRepository.getPlan(type.id).first().asPlanItem())
            }

            is ViewIdType.ReviewId -> {
                emit(reviewRepository.getReview(type.id).first().asPlanItem())
            }

            is ViewIdType.PostId -> {
                emit(reviewRepository.getReviewForPostId(type.id).first().asPlanItem())
            }

            else -> throw IllegalStateException("this ViewTypeId is not allowed")
        }
    }.flowOn(ioDispatcher)

    data class Params(
        val viewIdType: ViewIdType,
    )
}