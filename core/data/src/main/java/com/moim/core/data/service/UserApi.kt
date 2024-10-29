package com.moim.core.data.service

import com.moim.core.data.model.UserResponse
import retrofit2.http.GET

internal interface UserApi {

    @GET("user/info")
    suspend fun getUser(): UserResponse
}