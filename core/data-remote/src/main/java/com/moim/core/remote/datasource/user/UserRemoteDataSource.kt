package com.moim.core.remote.datasource.user

import com.moim.core.remote.model.UserResponse
import kotlinx.serialization.json.JsonObject

interface UserRemoteDataSource {
    suspend fun getUser(): UserResponse

    suspend fun updateUser(params: JsonObject): UserResponse

    suspend fun deleteUser()

    suspend fun checkedNickname(nickname: String): Boolean
}
