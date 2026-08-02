package com.moim.core.remote.datasource.auth

import com.moim.core.remote.model.TokenResponse
import kotlinx.serialization.json.JsonObject

interface AuthRemoteDataSource {
    suspend fun signIn(params: JsonObject): TokenResponse

    suspend fun signUp(params: JsonObject): TokenResponse

    suspend fun signOut(
        token: String,
        params: JsonObject,
    )
}
