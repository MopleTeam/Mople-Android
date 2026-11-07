package com.moim.core.data.datasource.token

interface TokenRepository {
    suspend fun setFcmToken()

    suspend fun syncFcmTokenIfNeeded()

    suspend fun onFcmTokenRefreshed(fcmToken: String)
}
