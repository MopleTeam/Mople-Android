package com.moim.core.remote.datasource.token

import com.moim.core.remote.di.qualifiers.MoimApi
import com.moim.core.remote.util.postJson
import io.ktor.client.HttpClient
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

internal class TokenRemoteDataSourceImpl @Inject constructor(
    @MoimApi private val client: HttpClient,
) : TokenRemoteDataSource {
    override suspend fun setFcmToken(params: JsonObject) {
        client.postJson("token/save", params)
    }
}
