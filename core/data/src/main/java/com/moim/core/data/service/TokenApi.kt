package com.moim.core.data.service

import com.moim.core.datamodel.TokenResponse
import retrofit2.http.Header
import retrofit2.http.POST

internal interface TokenApi {

    @POST("auth/recreate")
    suspend fun getRefreshToken(@Header("Refresh") refreshToken: String?): TokenResponse
}