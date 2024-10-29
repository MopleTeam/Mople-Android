package com.moim.core.data.service

import com.moim.core.data.model.TokenResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST

internal interface AuthApi {

    @POST("auth/sign-in")
    suspend fun signIn(@Body params: JsonObject): TokenResponse

    @POST("auth/sign-up")
    suspend fun signUp(@Body params: JsonObject): TokenResponse
}