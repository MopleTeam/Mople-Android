package com.moim.core.data.datasource.meeting

import com.moim.core.common.model.Meeting
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.User
import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.remote.datasource.image.ImageUploadRemoteDataSource
import com.moim.core.remote.datasource.meeting.MeetingRemoteDataSource
import com.moim.core.remote.model.MeetingResponse
import com.moim.core.remote.model.UserResponse
import com.moim.core.remote.model.asItem
import javax.inject.Inject

internal class MeetingRepositoryImpl @Inject constructor(
    private val meetingRemoteDataSource: MeetingRemoteDataSource,
    private val imageUploadRemoteDataSource: ImageUploadRemoteDataSource,
) : MeetingRepository {
    override suspend fun getMeetings(
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Meeting>> =
        meetingRemoteDataSource
            .getMeetings(
                cursor = cursor,
                size = size,
            ).asItem {
                it.map(MeetingResponse::asItem)
            }

    override suspend fun getMeetingsForHost(
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Meeting>> =
        meetingRemoteDataSource
            .getMeetingsForHost(
                cursor = cursor,
                size = size,
            ).asItem {
                it.map(MeetingResponse::asItem)
            }

    override suspend fun getMeeting(meetingId: String): Meeting = meetingRemoteDataSource.getMeeting(meetingId).asItem()

    override suspend fun getMeetingInviteCode(meetingId: String): String = meetingRemoteDataSource.getMeetingInviteCode(meetingId)

    override suspend fun getMeetingParticipants(
        meetingId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<User>> =
        meetingRemoteDataSource
            .getMeetingParticipants(
                id = meetingId,
                cursor = cursor,
                size = size,
            ).asItem {
                it.map(UserResponse::asItem)
            }

    override suspend fun getMeetingParticipantsForSearch(
        meetingId: String,
        keyword: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<User>> =
        meetingRemoteDataSource
            .getMeetingParticipantsForSearch(
                id = meetingId,
                keyword = keyword,
                cursor = cursor,
                size = size,
            ).asItem {
                it.map(UserResponse::asItem)
            }

    override suspend fun createMeeting(
        meetingName: String,
        meetingImageUrl: String?,
    ): Meeting {
        val uploadImageUrl = imageUploadRemoteDataSource.uploadImage(meetingImageUrl, "meet")

        return meetingRemoteDataSource.createMeeting(jsonOf(KEY_NAME to meetingName, KEY_IMAGE to uploadImageUrl)).asItem()
    }

    override suspend fun updateMeeting(
        meetingId: String,
        meetingName: String,
        meetingImageUrl: String?,
    ): Meeting {
        val uploadImageUrl = imageUploadRemoteDataSource.uploadImage(meetingImageUrl, "meet")

        return meetingRemoteDataSource
            .updateMeeting(
                id = meetingId,
                params =
                    jsonOf(
                        KEY_NAME to meetingName,
                        KEY_IMAGE to uploadImageUrl,
                    ),
            ).asItem()
    }

    override suspend fun updateMeetingLeader(
        meetingId: String,
        newHostId: String,
    ) {
        meetingRemoteDataSource.updateMeetingLeader(
            id = meetingId,
            params =
                jsonOf(
                    KEY_NEW_HOST_ID to newHostId,
                ),
        )
    }

    override suspend fun joinMeeting(code: String): Meeting = meetingRemoteDataSource.joinMeeting(code).asItem()

    override suspend fun deleteMeeting(meetingId: String) {
        meetingRemoteDataSource.deleteMeeting(meetingId)
    }

    companion object {
        private const val KEY_NAME = "name"
        private const val KEY_IMAGE = "image"
        private const val KEY_NEW_HOST_ID = "newHostId"
    }
}
