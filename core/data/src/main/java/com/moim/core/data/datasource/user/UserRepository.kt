package com.moim.core.data.datasource.user

import com.moim.core.common.model.Theme
import com.moim.core.common.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(): Flow<User>

    fun fetchUser(): Flow<User>

    fun updateUser(
        profileUrl: String?,
        nickname: String,
    ): Flow<User>

    fun deleteUser(): Flow<Unit>

    fun checkedNickname(nickname: String): Flow<Boolean>

    fun getTheme(): Flow<Theme>

    suspend fun setTheme(value: Theme)

    suspend fun clearMoimStorage()
}
