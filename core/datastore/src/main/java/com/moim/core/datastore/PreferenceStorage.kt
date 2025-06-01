package com.moim.core.datastore

import com.moim.core.datamodel.TokenResponse
import com.moim.core.datamodel.UserResponse
import kotlinx.coroutines.flow.Flow

interface PreferenceStorage {

    val user: Flow<UserResponse?>

    suspend fun saveUser(user: UserResponse)

    val token: Flow<TokenResponse?>

    suspend fun saveUserToken(token: TokenResponse)

    suspend fun clearMoimStorage()
}