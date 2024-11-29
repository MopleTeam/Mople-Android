package com.moim.core.data.datasource.plan

import com.moim.core.data.datasource.plan.remote.PlanRemoteDataSource
import com.moim.core.data.model.PlaceResponse
import com.moim.core.data.model.MeetingPlanContainer
import com.moim.core.data.model.PlanResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class PlanRepositoryImpl @Inject constructor(
    private val remoteDataSource: PlanRemoteDataSource
) : PlanRepository {

    override fun getCurrentPlans(): Flow<MeetingPlanContainer> = flow {
        emit(remoteDataSource.getCurrentPlan())
    }

    override fun getPlans(meetingId: String): Flow<List<PlanResponse>> = flow {
        emit(remoteDataSource.getPlans(meetingId))
    }

    override fun getPlan(planId: String): Flow<PlanResponse> = flow {
        emit(remoteDataSource.getPlan(planId))
    }

    override fun getPlansForCalendar(page: Int, yearAndMonth: String, isClosed: Boolean): Flow<List<PlanResponse>> = flow {
        emit(remoteDataSource.getPlansForCalendar(page, yearAndMonth, isClosed))
    }

    override fun getSearchPlace(keyword: String, xPoint: String, yPoint: String): Flow<List<PlaceResponse>> = flow {
        emit(remoteDataSource.getSearchPlace(keyword, xPoint, yPoint))
    }

    override fun createPlan(
        meetingId: String,
        planName: String,
        planTime: String,
        planAddress: String,
        longitude: Double,
        latitude: Double
    ): Flow<PlanResponse> = flow {
        emit(remoteDataSource.createPlan(meetingId, planName, planTime, planAddress, longitude, latitude))
    }
}