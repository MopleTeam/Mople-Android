package com.moim.core.data.datasource.plan

import com.moim.core.data.datasource.plan.remote.PlanRemoteDataSource
import com.moim.core.datamodel.PlaceResponse
import com.moim.core.datamodel.PlanResponse
import com.moim.core.model.asItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class PlanRepositoryImpl @Inject constructor(
    private val remoteDataSource: PlanRemoteDataSource
) : PlanRepository {

    override fun getCurrentPlans() = flow {
        emit(remoteDataSource.getCurrentPlan().asItem())
    }

    override fun getPlans(meetingId: String) = flow {
        emit(remoteDataSource.getPlans(meetingId).map(PlanResponse::asItem))
    }

    override fun getPlan(planId: String) = flow {
        emit(remoteDataSource.getPlan(planId).asItem())
    }

    override fun getPlansForCalendar(page: Int, yearAndMonth: String, isClosed: Boolean) = flow {
        emit(remoteDataSource.getPlansForCalendar(page, yearAndMonth, isClosed).map(PlanResponse::asItem))
    }

    override fun getSearchPlace(keyword: String, xPoint: String, yPoint: String) = flow {
        emit(remoteDataSource.getSearchPlace(keyword, xPoint, yPoint).map(PlaceResponse::asItem))
    }

    override fun createPlan(
        meetingId: String,
        planName: String,
        planTime: String,
        planAddress: String,
        longitude: Double,
        latitude: Double
    ) = flow {
        emit(remoteDataSource.createPlan(meetingId, planName, planTime, planAddress, longitude, latitude).asItem())
    }

    override fun updatePlan(
        planId: String,
        planName: String,
        planTime: String,
        planAddress: String,
        longitude: Double,
        latitude: Double
    ) = flow {
        emit(remoteDataSource.updatePlan(planId, planName, planTime, planAddress, longitude, latitude).asItem())
    }

    override fun deletePlan(planId: String) = flow {
        emit(remoteDataSource.deletePlan(planId))
    }

    override fun reportPlan(planId: String) = flow {
        emit(remoteDataSource.reportPlan(planId))
    }
}