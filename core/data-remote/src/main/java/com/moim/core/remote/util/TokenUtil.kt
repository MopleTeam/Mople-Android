package com.moim.core.remote.util

import com.moim.core.common.exception.ForbiddenException
import com.moim.core.common.exception.UnAuthorizedException
import com.moim.core.common.model.Token
import com.moim.core.remote.datasource.auth.AuthTokenRemoteDataSource
import com.moim.core.remote.model.asItem
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

// 동시에 401을 받아도 갱신은 한 번만 수행되도록 직렬화한다.
@Singleton
internal class TokenManager @Inject constructor(
    private val userDataUtil: UserDataUtil,
    private val authTokenRemoteDataSource: AuthTokenRemoteDataSource,
) {
    private val mutex: Mutex = Mutex()

    suspend fun currentAccessToken(): String? = currentTokenOrNull()?.accessToken

    // 갱신 불가 시 null
    suspend fun refreshAccessToken(failedAccessToken: String?): String? =
        mutex.withLock {
            val storedToken = currentTokenOrNull()

            if (storedToken != null && storedToken.accessToken != failedAccessToken) {
                Timber.d("[TokenManager] 이미 갱신된 토큰으로 재시도합니다.")
                return@withLock storedToken.accessToken
            }

            val refreshToken = storedToken?.refreshToken
            if (refreshToken.isNullOrBlank()) {
                Timber.w("[TokenManager] refreshToken이 없어 세션을 종료합니다.")
                clearUserSessionSafely()
                return@withLock null
            }

            refreshOrNull(refreshToken)?.accessToken
        }

    private suspend fun refreshOrNull(refreshToken: String): Token? =
        try {
            authTokenRemoteDataSource
                .getRefreshToken(refreshToken)
                .asItem()
                .also { userDataUtil.saveUserToken(it) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: UnAuthorizedException) {
            // refreshToken이 만료·무효할 때만 세션 정리
            Timber.e("[TokenManager] refreshToken 만료: ${e.message}")
            clearUserSessionSafely()
            null
        } catch (e: ForbiddenException) {
            Timber.e("[TokenManager] refreshToken 무효: ${e.message}")
            clearUserSessionSafely()
            null
        } catch (e: Exception) {
            // 일시적 실패로는 세션을 지우지 않는다.
            Timber.e("[TokenManager] 토큰 갱신 실패: ${e.message}")
            null
        }

    private suspend fun currentTokenOrNull(): Token? =
        try {
            userDataUtil.token.first()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.e("[TokenManager] 토큰 조회 실패: ${e.message}")
            null
        }

    private suspend fun clearUserSessionSafely() {
        try {
            userDataUtil.clearUserSession()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.e("[TokenManager] 세션 정리 실패: ${e.message}")
        }
    }
}

// 클라이언트 생성 후 등록해야 응답 검증(HttpSend 인터셉터)보다 안쪽에서 401을 먼저 본다.
internal fun HttpClient.installTokenRefresh(tokenManager: TokenManager) {
    plugin(HttpSend).intercept { request ->
        var accessToken = tokenManager.currentAccessToken()
        if (!accessToken.isNullOrEmpty()) {
            request.headers[HEADER_AUTHORIZATION] = accessToken.convertToToken()
        }

        var call = execute(request)
        var retryCount = 0

        while (call.response.status == HttpStatusCode.Unauthorized && retryCount < MAX_RETRY_COUNT) {
            val newAccessToken = tokenManager.refreshAccessToken(accessToken) ?: break

            accessToken = newAccessToken
            request.headers[HEADER_AUTHORIZATION] = newAccessToken.convertToToken()
            call = execute(request)
            retryCount++
        }

        call
    }
}

private const val MAX_RETRY_COUNT = 2

internal const val HEADER_AUTHORIZATION = "Authorization"
internal const val HEADER_REFRESH = "Refresh"

fun String?.convertToToken(): String = "Bearer $this"
