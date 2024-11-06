package com.moim.core.data.service

import com.moim.core.data.model.UserResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface UserApi {

    @GET("user/info")
    suspend fun getUser(): UserResponse

    @GET("user/nickname/duplicate")
    suspend fun checkedNickname(@Query("nickname") nickname: String): Boolean
}