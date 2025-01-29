package com.moim.core.data.datasource.meeting

import com.moim.core.model.Meeting
import kotlinx.coroutines.flow.Flow

interface MeetingRepository {

    fun getMeetings(): Flow<List<Meeting>>

    fun getMeeting(meetingId: String): Flow<Meeting>

    fun createMeeting(meetingName: String, meetingImageUrl: String?): Flow<Meeting>

    fun updateMeeting(meetingId: String, meetingName: String, meetingImageUrl: String?): Flow<Meeting>

    fun deleteMeeting(meetingId: String) : Flow<Unit>
}