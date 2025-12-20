package com.moim.core.data.datasource.plan

import com.moim.core.common.model.MeetingPlanContainer
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.Place
import com.moim.core.common.model.Plan
import com.moim.core.common.model.PlanReviewContainer
import com.moim.core.common.model.User
import kotlinx.coroutines.flow.Flow

interface PlanRepository {

    fun getCurrentPlans(): Flow<MeetingPlanContainer>

    fun getPlan(planId: String): Flow<Plan>

    suspend fun getPlans(
        meetingId: String,
        cursor: String,
        size: Int
    ): PaginationContainer<List<Plan>>

    fun getPlansForCalendar(date: String): Flow<PlanReviewContainer>

    fun getSearchPlace(
        keyword: String,
        xPoint: String,
        yPoint: String
    ): Flow<List<Place>>

    suspend fun getPlanParticipants(
        planId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<User>>

    fun createPlan(
        meetingId: String,
        planName: String,
        planTime: String,
        planAddress: String?,
        planWeatherAddress: String?,
        planDescription: String?,
        title: String,
        longitude: Double?,
        latitude: Double?,
    ): Flow<Plan>

    fun joinPlan(
        planId: String,
    ): Flow<Unit>

    fun leavePlan(
        planId: String
    ): Flow<Unit>

    fun updatePlan(
        planId: String,
        planName: String,
        planTime: String,
        planAddress: String?,
        planWeatherAddress: String?,
        planDescription: String?,
        title: String,
        longitude: Double?,
        latitude: Double?,
    ): Flow<Plan>

    fun deletePlan(planId: String): Flow<Unit>

    fun reportPlan(planId: String): Flow<Unit>
}