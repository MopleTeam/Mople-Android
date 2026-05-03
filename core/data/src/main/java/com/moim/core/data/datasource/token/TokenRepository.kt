package com.moim.core.data.datasource.token

import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    fun setFcmToken(): Flow<Unit>

    fun syncFcmTokenIfNeeded(): Flow<Unit>

    suspend fun onFcmTokenRefreshed(fcmToken: String)
}
