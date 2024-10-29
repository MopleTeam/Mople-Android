package com.moim.core.data.service

import com.moim.core.data.model.TokenResponse
import retrofit2.http.POST
import retrofit2.http.Query

internal interface TokenApi {

    @POST("auth/recreate")
    suspend fun getRefreshToken(@Query("refreshToken") refreshToken: String): TokenResponse
}