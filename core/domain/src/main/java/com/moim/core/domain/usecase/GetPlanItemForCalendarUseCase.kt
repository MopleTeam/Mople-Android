package com.moim.core.domain.usecase

import com.moim.core.common.di.IoDispatcher
import com.moim.core.common.model.Plan
import com.moim.core.common.model.Review
import com.moim.core.common.model.item.asPlanItem
import com.moim.core.data.datasource.plan.PlanRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetPlanItemForCalendarUseCase @Inject constructor(
    private val planRepository: PlanRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    operator fun invoke(date: String) = flow {
        val planAndReview = planRepository.getPlansForCalendar(date).first()
        emit(planAndReview.plans.map(Plan::asPlanItem) + planAndReview.reviews.map(Review::asPlanItem))
    }.flowOn(ioDispatcher)
}