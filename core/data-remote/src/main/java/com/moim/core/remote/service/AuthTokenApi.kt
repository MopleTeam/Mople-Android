package com.moim.core.remote.service

import com.moim.core.remote.model.TokenResponse
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthTokenApi {
    @POST("auth/recreate")
    suspend fun getRefreshToken(
        @Header("Refresh") refreshToken: String?,
    ): TokenResponse
}
