package com.moim.core.data.datasource.meeting

import com.moim.core.data.model.MeetingPlanResponse
import com.moim.core.data.model.MeetingResponse
import kotlinx.coroutines.flow.Flow

interface MeetingRepository {

    fun getMeetings(): Flow<List<MeetingResponse>>

    fun getMeeting(meetingId: String): Flow<MeetingResponse>

    fun getMeetingPlans(page: Int, yearAndMonth: String, isClosed: Boolean): Flow<List<MeetingPlanResponse>>

    fun createMeeting(meetingName: String, meetingImageUrl: String?): Flow<MeetingResponse>

    fun updateMeeting(meetingId: String, meetingName: String, meetingImageUrl: String?): Flow<MeetingResponse>
}