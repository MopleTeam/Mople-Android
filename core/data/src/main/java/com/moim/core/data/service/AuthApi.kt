package com.moim.core.data.service

import com.moim.core.datamodel.TokenResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

internal interface AuthApi {

    @POST("auth/sign-in")
    suspend fun signIn(@Body params: JsonObject): TokenResponse

    @POST("auth/sign-up")
    suspend fun signUp(@Body params: JsonObject): TokenResponse

    @POST("auth/sign-out")
    suspend fun signOut(
        @Header("Authorization") token : String,
        @Body params: JsonObject
    )
}