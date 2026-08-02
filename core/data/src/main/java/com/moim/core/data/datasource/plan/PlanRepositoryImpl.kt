package com.moim.core.data.datasource.plan

import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.Plan
import com.moim.core.common.model.PlanReviewContainer
import com.moim.core.common.model.User
import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.remote.datasource.location.LocationRemoteDataSource
import com.moim.core.remote.datasource.plan.PlanRemoteDataSource
import com.moim.core.remote.model.PlaceResponse
import com.moim.core.remote.model.PlanResponse
import com.moim.core.remote.model.UserResponse
import com.moim.core.remote.model.asItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class PlanRepositoryImpl @Inject constructor(
    private val planRemoteDataSource: PlanRemoteDataSource,
    private val locationRemoteDataSource: LocationRemoteDataSource,
) : PlanRepository {
    override fun getCurrentPlans() =
        flow {
            emit(planRemoteDataSource.getCurrentPlan().asItem())
        }

    override suspend fun getPlans(
        meetingId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Plan>> =
        planRemoteDataSource
            .getPlans(
                id = meetingId,
                cursor = cursor,
                size = size,
            ).asItem { it.map(PlanResponse::asItem) }

    override fun getPlan(planId: String) =
        flow {
            emit(planRemoteDataSource.getPlan(planId).asItem())
        }

    override fun getPlansForCalendar(date: String): Flow<PlanReviewContainer> =
        flow {
            emit(planRemoteDataSource.getPlansForCalendar(date).asItem())
        }

    override fun getSearchPlace(
        keyword: String,
        xPoint: String,
        yPoint: String,
    ) = flow {
        emit(
            locationRemoteDataSource
                .getSearchLocation(
                    params =
                        jsonOf(
                            KEY_QUERY to keyword,
                            KEY_X_POINT to xPoint,
                            KEY_Y_POINT to yPoint,
                        ),
                ).locations
                .map(PlaceResponse::asItem),
        )
    }

    override suspend fun getPlanParticipants(
        planId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<User>> =
        planRemoteDataSource
            .getPlanParticipants(
                planId = planId,
                cursor = cursor,
                size = size,
            ).asItem {
                it.map(UserResponse::asItem)
            }

    override fun joinPlan(planId: String) =
        flow {
            emit(planRemoteDataSource.joinPlan(planId))
        }

    override fun leavePlan(planId: String) =
        flow {
            emit(planRemoteDataSource.leavePlan(planId))
        }

    override fun createPlan(
        meetingId: String,
        planName: String,
        planTime: String,
        planAddress: String?,
        planWeatherAddress: String?,
        planDescription: String?,
        title: String,
        longitude: Double?,
        latitude: Double?,
    ) = flow {
        emit(
            planRemoteDataSource
                .createPlan(
                    jsonOf(
                        KEY_MEETING_ID to meetingId,
                        KEY_NAME to planName,
                        KEY_PLAN_TIME to planTime,
                        KEY_PLAN_ADDRESS to planAddress,
                        KEY_TITLE to title,
                        KEY_LOT to longitude,
                        KEY_LAT to latitude,
                        KEY_WEATHER_ADDRESS to planWeatherAddress,
                        KEY_DESCRIPTION to planDescription,
                    ),
                ).asItem(),
        )
    }

    override fun updatePlan(
        planId: String,
        planName: String,
        planTime: String,
        planAddress: String?,
        planWeatherAddress: String?,
        planDescription: String?,
        title: String,
        longitude: Double?,
        latitude: Double?,
    ) = flow {
        emit(
            planRemoteDataSource
                .updatePlan(
                    jsonOf(
                        KEY_PLAN_ID to planId,
                        KEY_NAME to planName,
                        KEY_PLAN_TIME to planTime,
                        KEY_PLAN_ADDRESS to planAddress,
                        KEY_LOT to longitude,
                        KEY_LAT to latitude,
                        KEY_WEATHER_ADDRESS to planWeatherAddress,
                        KEY_DESCRIPTION to planDescription,
                    ),
                ).asItem(),
        )
    }

    override fun deletePlan(planId: String) =
        flow {
            emit(planRemoteDataSource.deletePlan(planId))
        }

    override fun reportPlan(planId: String) =
        flow {
            emit(
                planRemoteDataSource.reportPlan(
                    jsonOf(
                        KEY_PLAN_ID to planId,
                        KEY_REASON to "",
                    ),
                ),
            )
        }

    companion object {
        private const val KEY_QUERY = "query"
        private const val KEY_MEETING_ID = "meetId"
        private const val KEY_PLAN_ID = "planId"
        private const val KEY_PLAN_TIME = "planTime"
        private const val KEY_PLAN_ADDRESS = "planAddress"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_WEATHER_ADDRESS = "weatherAddress"
        private const val KEY_NAME = "name"
        private const val KEY_TITLE = "title"
        private const val KEY_X_POINT = "x"
        private const val KEY_Y_POINT = "y"
        private const val KEY_LOT = "lot"
        private const val KEY_LAT = "lat"
        private const val KEY_REASON = "reason"
    }
}
