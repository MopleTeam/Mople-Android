package com.moim.core.data.datasource.token

import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.crashreport.CrashReporter
import com.moim.core.data.util.catchFlow
import com.moim.core.local.PreferenceStorage
import com.moim.core.remote.service.TokenApi
import com.moim.core.remote.util.FirebaseUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class TokenRepositoryImpl @Inject constructor(
    private val tokenApi: TokenApi,
    private val preferenceStorage: PreferenceStorage,
    private val crashReporter: CrashReporter,
) : TokenRepository {
    override fun setFcmToken(): Flow<Unit> =
        catchFlow {
            val fcmToken = FirebaseUtil.getFirebaseMessageToken()
            sendFcmToken(fcmToken)
            emit(Unit)
        }

    override fun syncFcmTokenIfNeeded(): Flow<Unit> =
        catchFlow {
            val current = FirebaseUtil.getFirebaseMessageToken()
            val lastSent = preferenceStorage.lastFcmToken.first()
            if (current != null && current != lastSent) {
                sendFcmToken(current)
            }
            emit(Unit)
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
        tokenApi.setFcmToken(
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
