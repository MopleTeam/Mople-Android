package com.moim.core.data.datasource.meeting

import com.moim.core.common.model.Meeting
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.User
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

    suspend fun getMeetingParticipantsForSearch(
        meetingId: String,
        keyword: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<User>>

    fun createMeeting(
        meetingName: String,
        meetingImageUrl: String?,
    ): Flow<Meeting>

    fun updateMeeting(
        meetingId: String,
        meetingName: String,
        meetingImageUrl: String?,
    ): Flow<Meeting>

    fun updateMeetingLeader(
        meetingId: String,
        newHostId: String,
    ): Flow<Unit>

    fun joinMeeting(code: String): Flow<Meeting>

    fun deleteMeeting(meetingId: String): Flow<Unit>
}
