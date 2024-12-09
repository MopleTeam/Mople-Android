package com.moim.core.data.datasource.user

import com.moim.core.datamodel.UserResponse
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUser(): Flow<UserResponse>

    fun fetchUser(): Flow<UserResponse>

    fun updateUser(profileUrl: String?, nickname: String): Flow<UserResponse>

    fun deleteUser(): Flow<Unit>

    fun checkedNickname(nickname: String): Flow<Boolean>

    suspend fun clearMoimStorage()
}