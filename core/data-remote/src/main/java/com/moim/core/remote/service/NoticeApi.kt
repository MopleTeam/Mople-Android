package com.moim.core.remote.service

import com.moim.core.remote.model.NoticeResponse
import com.moim.core.remote.model.PaginationContainerResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NoticeApi {
    @GET("notice/list/{meetId}")
    suspend fun getNotices(
        @Path("meetId") meetId: String,
        @Query("cursor") cursor: String,
        @Query("size") size: Int,
    ): PaginationContainerResponse<List<NoticeResponse>>

    @POST("notice/create")
    suspend fun createNotice(
        @Body params: JsonObject,
    ): NoticeResponse

    @PATCH("notice/update/{noticeId}")
    suspend fun updateNotice(
        @Path("noticeId") noticeId: String,
        @Body params: JsonObject,
    ): NoticeResponse

    @DELETE("notice/{noticeId}")
    suspend fun deleteNotice(
        @Path("noticeId") noticeId: String,
    )

    @PATCH("notice/pin/{noticeId}")
    suspend fun pinNotice(
        @Path("noticeId") noticeId: String,
    ): NoticeResponse

    @DELETE("notice/pin/{noticeId}")
    suspend fun unpinNotice(
        @Path("noticeId") noticeId: String,
    ): NoticeResponse
}
