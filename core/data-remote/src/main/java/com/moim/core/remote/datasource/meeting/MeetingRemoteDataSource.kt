package com.moim.core.remote.datasource.meeting

import com.moim.core.remote.model.MeetingResponse
import com.moim.core.remote.model.PaginationContainerResponse
import com.moim.core.remote.model.UserResponse
import kotlinx.serialization.json.JsonObject

interface MeetingRemoteDataSource {
    suspend fun getMeetings(
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<MeetingResponse>>

    suspend fun getMeetingsForHost(
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<MeetingResponse>>

    suspend fun getMeeting(id: String): MeetingResponse

    suspend fun getMeetingInviteCode(id: String): String

    suspend fun getMeetingParticipants(
        id: String,
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<UserResponse>>

    suspend fun getMeetingParticipantsForSearch(
        id: String,
        keyword: String,
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<UserResponse>>

    suspend fun createMeeting(params: JsonObject): MeetingResponse

    suspend fun updateMeeting(
        id: String,
        params: JsonObject,
    ): MeetingResponse

    suspend fun updateMeetingLeader(
        id: String,
        params: JsonObject,
    )

    suspend fun joinMeeting(meetCode: String): MeetingResponse

    suspend fun deleteMeeting(id: String)
}
