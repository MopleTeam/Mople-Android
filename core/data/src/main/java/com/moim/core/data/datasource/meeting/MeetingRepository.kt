package com.moim.core.data.datasource.meeting

import com.moim.core.model.Meeting
import com.moim.core.model.PaginationContainer
import com.moim.core.model.User
import kotlinx.coroutines.flow.Flow

interface MeetingRepository {

    suspend fun getMeetings(
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Meeting>>

    fun getMeeting(meetingId: String): Flow<Meeting>

    fun getMeetingInviteCode(meetingId: String): Flow<String>

    suspend fun getMeetingParticipants(
        meetingId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<User>>

    fun createMeeting(meetingName: String, meetingImageUrl: String?): Flow<Meeting>

    fun updateMeeting(meetingId: String, meetingName: String, meetingImageUrl: String?): Flow<Meeting>

    fun joinMeeting(code: String): Flow<Meeting>

    fun deleteMeeting(meetingId: String): Flow<Unit>
}