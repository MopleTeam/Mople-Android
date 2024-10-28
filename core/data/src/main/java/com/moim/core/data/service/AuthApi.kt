package com.moim.core.data.service

import com.moim.core.data.model.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

internal interface AuthApi {

    @POST("auth/sign-in")
    suspend fun signIn(@Body params: Any): TokenResponse

    @POST("auth/sign-up")
    suspend fun signUp(@Body params: Any): TokenResponse

    @POST("auth/recreate")
    suspend fun getRefreshToken(@Query("refreshToken") refreshToken: String): TokenResponse
}