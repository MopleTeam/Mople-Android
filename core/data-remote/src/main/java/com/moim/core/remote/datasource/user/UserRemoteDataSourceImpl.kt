package com.moim.core.remote.datasource.user

import com.moim.core.remote.di.qualifiers.MoimApi
import com.moim.core.remote.model.UserResponse
import com.moim.core.remote.util.patchJson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

internal class UserRemoteDataSourceImpl @Inject constructor(
    @MoimApi private val client: HttpClient,
) : UserRemoteDataSource {
    override suspend fun getUser(): UserResponse = client.get("user/info").body()

    override suspend fun updateUser(params: JsonObject): UserResponse = client.patchJson("user/info", params).body()

    override suspend fun deleteUser() {
        client.delete("user/remove")
    }

    override suspend fun checkedNickname(nickname: String): Boolean =
        client
            .get("user/nickname/duplicate") {
                parameter("nickname", nickname)
            }.body()
}
