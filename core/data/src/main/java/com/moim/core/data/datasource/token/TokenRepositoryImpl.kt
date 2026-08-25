package com.moim.core.data.datasource.token

import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.crashreport.CrashReporter
import com.moim.core.local.PreferenceStorage
import com.moim.core.remote.datasource.token.TokenRemoteDataSource
import com.moim.core.remote.util.FirebaseUtil
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class TokenRepositoryImpl @Inject constructor(
    private val tokenRemoteDataSource: TokenRemoteDataSource,
    private val preferenceStorage: PreferenceStorage,
    private val crashReporter: CrashReporter,
) : TokenRepository {
    override suspend fun setFcmToken() {
        val fcmToken = FirebaseUtil.getFirebaseMessageToken()
        sendFcmToken(fcmToken)
    }

    override suspend fun syncFcmTokenIfNeeded() {
        val current = FirebaseUtil.getFirebaseMessageToken()
        val lastSent = preferenceStorage.lastFcmToken.first()
        if (current != null && current != lastSent) {
            sendFcmToken(current)
        }
    }

    override suspend fun onFcmTokenRefreshed(fcmToken: String) {
        val isLoggedIn = preferenceStorage.token.first() != null
        if (!isLoggedIn) return
        runCatching {
            sendFcmToken(fcmToken)
        }.onFailure {
            crashReporter.logException(it)
        }
    }

    private suspend fun sendFcmToken(fcmToken: String?) {
        tokenRemoteDataSource.setFcmToken(
            jsonOf(
                KEY_TOKEN to fcmToken,
                KEY_SUBSCRIBE to true,
            ),
        )
        if (fcmToken != null) {
            preferenceStorage.saveLastFcmToken(fcmToken)
        }
    }

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_SUBSCRIBE = "subscribe"
    }
}
