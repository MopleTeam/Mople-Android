package com.moim.core.local

import com.moim.core.common.model.Theme
import com.moim.core.common.model.Token
import com.moim.core.common.model.User
import kotlinx.coroutines.flow.Flow

interface PreferenceStorage {
    val user: Flow<User?>

    suspend fun saveUser(user: User)

    val token: Flow<Token?>

    suspend fun saveUserToken(token: Token)

    val lastFcmToken: Flow<String?>

    suspend fun saveLastFcmToken(fcmToken: String)

    fun getTheme(): Flow<Theme>

    suspend fun setTheme(value: Theme)

    suspend fun clearMoimStorage()
}
