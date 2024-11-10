package com.moim.core.data.datasource.meeting.remote

import com.moim.core.data.model.MeetingPlanResponse
import com.moim.core.data.model.MeetingResponse

internal interface MeetingRemoteDataSource {

    suspend fun getMeetings(): List<MeetingResponse>

    suspend fun getMeeting(meetingId: String): MeetingResponse

    suspend fun getMeetingPlans(page: Int, yearAndMonth: String, isClosed: Boolean): List<MeetingPlanResponse>

    suspend fun createMeeting(meetingName: String, meetingImageUrl: String?): MeetingResponse

    suspend fun updateMeeting(meetingId: String, meetingName: String, meetingImageUrl: String?): MeetingResponse
}