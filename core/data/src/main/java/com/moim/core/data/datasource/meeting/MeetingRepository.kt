package com.moim.core.data.datasource.meeting

import com.moim.core.common.model.Meeting
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.User

interface MeetingRepository {
    suspend fun getMeetings(
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Meeting>>

    suspend fun getMeetingsForHost(
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Meeting>>

    suspend fun getMeeting(meetingId: String): Meeting

    suspend fun getMeetingInviteCode(meetingId: String): String

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

    suspend fun createMeeting(
        meetingName: String,
        meetingImageUrl: String?,
    ): Meeting

    suspend fun updateMeeting(
        meetingId: String,
        meetingName: String,
        meetingImageUrl: String?,
    ): Meeting

    suspend fun updateMeetingLeader(
        meetingId: String,
        newHostId: String,
    )

    suspend fun joinMeeting(code: String): Meeting

    suspend fun deleteMeeting(meetingId: String)
}
