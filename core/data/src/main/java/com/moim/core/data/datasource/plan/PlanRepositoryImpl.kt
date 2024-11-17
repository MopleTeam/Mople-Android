package com.moim.core.data.datasource.plan

import com.moim.core.data.datasource.plan.remote.PlanRemoteDataSource
import com.moim.core.data.model.MeetingPlanContainer
import com.moim.core.data.model.MeetingPlanResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class PlanRepositoryImpl @Inject constructor(
    private val remoteDataSource: PlanRemoteDataSource
) : PlanRepository {

    override fun getCurrentPlans(): Flow<MeetingPlanContainer> = flow {
        emit(remoteDataSource.getCurrentPlan())
    }

    override fun getPlan(planId: String): Flow<MeetingPlanResponse> = flow {
        emit(remoteDataSource.getPlan(planId))
    }

    override fun getPlans(page: Int, yearAndMonth: String, isClosed: Boolean): Flow<List<MeetingPlanResponse>> = flow {
        emit(remoteDataSource.getPlans(page, yearAndMonth, isClosed))
    }
}