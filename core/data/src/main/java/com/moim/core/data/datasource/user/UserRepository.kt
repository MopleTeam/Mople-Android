package com.moim.core.data.datasource.user

import com.moim.core.common.model.Theme
import com.moim.core.common.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(): Flow<User>

    suspend fun fetchUser(): User

    suspend fun updateUser(
        profileUrl: String?,
        nickname: String,
    ): User

    suspend fun deleteUser()

    suspend fun checkedNickname(nickname: String): Boolean

    fun getTheme(): Flow<Theme>

    suspend fun setTheme(value: Theme)

    suspend fun clearMoimStorage()
}
