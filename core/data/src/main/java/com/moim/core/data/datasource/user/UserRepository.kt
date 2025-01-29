package com.moim.core.data.datasource.user

import com.moim.core.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUser(): Flow<User>

    fun fetchUser(): Flow<User>

    fun updateUser(profileUrl: String?, nickname: String): Flow<User>

    fun deleteUser(): Flow<Unit>

    fun checkedNickname(nickname: String): Flow<Boolean>

    suspend fun clearMoimStorage()
}