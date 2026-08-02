package com.moim.core.data.datasource.meeting

import com.moim.core.common.model.Meeting
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.User
import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.data.util.catchFlow
import com.moim.core.remote.datasource.image.ImageUploadRemoteDataSource
import com.moim.core.remote.datasource.meeting.MeetingRemoteDataSource
import com.moim.core.remote.model.MeetingResponse
import com.moim.core.remote.model.UserResponse
import com.moim.core.remote.model.asItem
import com.moim.core.remote.util.converterException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class MeetingRepositoryImpl @Inject constructor(
    private val meetingRemoteDataSource: MeetingRemoteDataSource,
    private val imageUploadRemoteDataSource: ImageUploadRemoteDataSource,
) : MeetingRepository {
    override suspend fun getMeetings(
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Meeting>> =
        try {
            meetingRemoteDataSource
                .getMeetings(
                    cursor = cursor,
                    size = size,
                ).asItem {
                    it.map(MeetingResponse::asItem)
                }
        } catch (e: Exception) {
            throw converterException(e)
        }

    override suspend fun getMeetingsForHost(
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Meeting>> =
        try {
            meetingRemoteDataSource
                .getMeetingsForHost(
                    cursor = cursor,
                    size = size,
                ).asItem {
                    it.map(MeetingResponse::asItem)
                }
        } catch (e: Exception) {
            throw converterException(e)
        }

    override fun getMeeting(meetingId: String) =
        catchFlow {
            emit(meetingRemoteDataSource.getMeeting(meetingId).asItem())
        }

    override fun getMeetingInviteCode(meetingId: String) =
        catchFlow {
            emit(meetingRemoteDataSource.getMeetingInviteCode(meetingId))
        }

    override suspend fun getMeetingParticipants(
        meetingId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<User>> =
        try {
            meetingRemoteDataSource
                .getMeetingParticipants(
                    id = meetingId,
                    cursor = cursor,
                    size = size,
                ).asItem {
                    it.map(UserResponse::asItem)
                }
        } catch (e: Exception) {
            throw converterException(e)
        }

    override suspend fun getMeetingParticipantsForSearch(
        meetingId: String,
        keyword: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<User>> =
        try {
            meetingRemoteDataSource
                .getMeetingParticipantsForSearch(
                    id = meetingId,
                    keyword = keyword,
                    cursor = cursor,
                    size = size,
                ).asItem {
                    it.map(UserResponse::asItem)
                }
        } catch (e: Exception) {
            throw converterException(e)
        }

    override fun createMeeting(
        meetingName: String,
        meetingImageUrl: String?,
    ): Flow<Meeting> =
        catchFlow {
            val uploadImageUrl = imageUploadRemoteDataSource.uploadImage(meetingImageUrl, "meet")
            emit(meetingRemoteDataSource.createMeeting(jsonOf(KEY_NAME to meetingName, KEY_IMAGE to uploadImageUrl)).asItem())
        }

    override fun updateMeeting(
        meetingId: String,
        meetingName: String,
        meetingImageUrl: String?,
    ): Flow<Meeting> =
        catchFlow {
            val uploadImageUrl = imageUploadRemoteDataSource.uploadImage(meetingImageUrl, "meet")

            emit(
                meetingRemoteDataSource
                    .updateMeeting(
                        id = meetingId,
                        params =
                            jsonOf(
                                KEY_NAME to meetingName,
                                KEY_IMAGE to uploadImageUrl,
                            ),
                    ).asItem(),
            )
        }

    override fun updateMeetingLeader(
        meetingId: String,
        newHostId: String,
    ): Flow<Unit> =
        catchFlow {
            emit(
                meetingRemoteDataSource.updateMeetingLeader(
                    id = meetingId,
                    params =
                        jsonOf(
                            KEY_NEW_HOST_ID to newHostId,
                        ),
                ),
            )
        }

    override fun joinMeeting(code: String): Flow<Meeting> =
        catchFlow {
            emit(meetingRemoteDataSource.joinMeeting(code).asItem())
        }

    override fun deleteMeeting(meetingId: String): Flow<Unit> =
        catchFlow {
            emit(meetingRemoteDataSource.deleteMeeting(meetingId))
        }

    companion object {
        private const val KEY_NAME = "name"
        private const val KEY_IMAGE = "image"
        private const val KEY_NEW_HOST_ID = "newHostId"
    }
}
