package com.moim.core.remote.datasource.auth

import com.moim.core.remote.di.qualifiers.NormalApi
import com.moim.core.remote.model.TokenResponse
import com.moim.core.remote.util.HEADER_AUTHORIZATION
import com.moim.core.remote.util.postJson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

internal class AuthRemoteDataSourceImpl @Inject constructor(
    @NormalApi private val client: HttpClient,
) : AuthRemoteDataSource {
    override suspend fun signIn(params: JsonObject): TokenResponse = client.postJson("auth/sign-in", params).body()

    override suspend fun signUp(params: JsonObject): TokenResponse = client.postJson("auth/sign-up", params).body()

    override suspend fun signOut(
        token: String,
        params: JsonObject,
    ) {
        client.postJson("auth/sign-out", params) {
            header(HEADER_AUTHORIZATION, token)
        }
    }
}
