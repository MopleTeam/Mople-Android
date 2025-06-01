package com.moim.core.network.service

import com.moim.core.datamodel.TokenResponse
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthTokenApi {

    @POST("auth/recreate")
    suspend fun getRefreshToken(@Header("Refresh") refreshToken: String?): TokenResponse
}