package com.moim.core.local

import com.moim.core.common.model.Token
import com.moim.core.common.model.User
import kotlinx.coroutines.flow.Flow

interface PreferenceStorage {

    val user: Flow<User?>

    suspend fun saveUser(user: User)

    val token: Flow<Token?>

    suspend fun saveUserToken(token: Token)

    suspend fun clearMoimStorage()
}