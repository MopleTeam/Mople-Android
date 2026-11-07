package com.moim.core.data.datasource.plan

import com.moim.core.common.model.MeetingPlanContainer
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.Place
import com.moim.core.common.model.Plan
import com.moim.core.common.model.PlanReviewContainer
import com.moim.core.common.model.User

interface PlanRepository {
    suspend fun getCurrentPlans(): MeetingPlanContainer

    suspend fun getPlan(planId: String): Plan

    suspend fun getPlans(
        meetingId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Plan>>

    suspend fun getPlansForCalendar(date: String): PlanReviewContainer

    suspend fun getSearchPlace(
        keyword: String,
        xPoint: String,
        yPoint: String,
    ): List<Place>

    suspend fun getPlanParticipants(
        planId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<User>>

    suspend fun createPlan(
        meetingId: String,
        planName: String,
        planTime: String,
        planAddress: String?,
        planWeatherAddress: String?,
        planDescription: String?,
        title: String,
        longitude: Double?,
        latitude: Double?,
    ): Plan

    suspend fun joinPlan(planId: String)

    suspend fun leavePlan(planId: String)

    suspend fun updatePlan(
        planId: String,
        planName: String,
        planTime: String,
        planAddress: String?,
        planWeatherAddress: String?,
        planDescription: String?,
        title: String,
        longitude: Double?,
        latitude: Double?,
    ): Plan

    suspend fun deletePlan(planId: String)

    suspend fun reportPlan(planId: String)
}
