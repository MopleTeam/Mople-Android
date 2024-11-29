package com.moim.core.data.datasource.meeting

import com.moim.core.data.model.MeetingResponse
import kotlinx.coroutines.flow.Flow

interface MeetingRepository {

    fun getMeetings(): Flow<List<MeetingResponse>>

    fun getMeeting(meetingId: String): Flow<MeetingResponse>

    fun createMeeting(meetingName: String, meetingImageUrl: String?): Flow<MeetingResponse>

    fun updateMeeting(meetingId: String, meetingName: String, meetingImageUrl: String?): Flow<MeetingResponse>

    fun deleteMeeting(meetingId: String) : Flow<Unit>
}