package com.moim.core.data.datasource.meeting.remote

import com.moim.core.data.model.MeetingPlanResponse

internal interface MeetingRemoteDataSource {

    suspend fun getMeetingPlans(page: Int, yearAndMonth: String, isClosed: Boolean) : List<MeetingPlanResponse>
}