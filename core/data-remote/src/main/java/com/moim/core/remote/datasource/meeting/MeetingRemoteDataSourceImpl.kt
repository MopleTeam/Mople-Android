package com.moim.core.remote.datasource.meeting

import com.moim.core.remote.di.qualifiers.MoimApi
import com.moim.core.remote.model.MeetingResponse
import com.moim.core.remote.model.PaginationContainerResponse
import com.moim.core.remote.model.UserResponse
import com.moim.core.remote.util.decodeJsonString
import com.moim.core.remote.util.patchJson
import com.moim.core.remote.util.postJson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

internal class MeetingRemoteDataSourceImpl @Inject constructor(
    @MoimApi private val client: HttpClient,
    private val json: Json,
) : MeetingRemoteDataSource {
    override suspend fun getMeetings(
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<MeetingResponse>> =
        client
            .get("meet/list") {
                parameter("cursor", cursor)
                parameter("size", size)
            }.body()

    override suspend fun getMeetingsForHost(
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<MeetingResponse>> =
        client
            .get("/meet/host/list") {
                parameter("cursor", cursor)
                parameter("size", size)
            }.body()

    override suspend fun getMeeting(id: String): MeetingResponse = client.get("meet/$id").body()

    override suspend fun getMeetingInviteCode(id: String): String = client.postJson("meet/invite/$id").decodeJsonString(json)

    override suspend fun getMeetingParticipants(
        id: String,
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<UserResponse>> =
        client
            .get("meet/members/$id") {
                parameter("cursor", cursor)
                parameter("size", size)
            }.body()

    override suspend fun getMeetingParticipantsForSearch(
        id: String,
        keyword: String,
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<UserResponse>> =
        client
            .get("meet/members/search/$id") {
                parameter("keyword", keyword)
                parameter("cursor", cursor)
                parameter("size", size)
            }.body()

    override suspend fun createMeeting(params: JsonObject): MeetingResponse = client.postJson("meet/create", params).body()

    override suspend fun updateMeeting(
        id: String,
        params: JsonObject,
    ): MeetingResponse = client.patchJson("meet/update/$id", params).body()

    override suspend fun updateMeetingLeader(
        id: String,
        params: JsonObject,
    ) {
        client.patchJson("meet/host/$id", params)
    }

    override suspend fun joinMeeting(meetCode: String): MeetingResponse = client.postJson("meet/join/$meetCode").body()

    override suspend fun deleteMeeting(id: String) {
        client.delete("meet/$id")
    }
}
