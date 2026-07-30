package com.moim.core.remote.util

import com.moim.core.common.model.Token
import com.moim.core.remote.model.asItem
import com.moim.core.remote.service.AuthTokenApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

internal class TokenAuthenticator @Inject constructor(
    private val userDataUtil: UserDataUtil,
    private val authTokenApi: AuthTokenApi,
) : Authenticator {
    private val mutex: Mutex = Mutex()

    override fun authenticate(
        route: Route?,
        response: Response,
    ): Request? {
        if (response.retryCount() >= MAX_RETRY_COUNT) {
            Timber.w("[TokenAuthenticator] 재시도 한도(${MAX_RETRY_COUNT}회) 초과로 인증을 포기합니다.")
            return null
        }

        val failedAuthorization = response.request.header(HEADER_AUTHORIZATION)

        return runBlocking {
            mutex.withLock {
                val storedToken = currentTokenOrNull()

                if (storedToken != null && storedToken.accessToken.convertToToken() != failedAuthorization) {
                    Timber.d("[TokenAuthenticator] 이미 갱신된 토큰으로 재시도합니다.")
                    return@withLock response.request.withAuthorization(storedToken.accessToken)
                }

                val refreshToken = storedToken?.refreshToken
                if (refreshToken.isNullOrBlank()) {
                    Timber.w("[TokenAuthenticator] refreshToken이 없어 세션을 종료합니다.")
                    clearUserSessionSafely()
                    return@withLock null
                }

                val newToken = refreshOrNull(refreshToken) ?: return@withLock null
                response.request.withAuthorization(newToken.accessToken)
            }
        }
    }

    private suspend fun refreshOrNull(refreshToken: String): Token? =
        try {
            authTokenApi
                .getRefreshToken(refreshToken)
                .asItem()
                .also { userDataUtil.saveUserToken(it) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            Timber.e("[TokenAuthenticator] 토큰 갱신 실패 (code=${e.code()}): ${e.message()}")
            // refreshToken 자체가 만료·무효한 경우에만 세션을 정리해 로그인 화면으로 되돌립니다.
            if (e.code() in SESSION_EXPIRED_CODES) clearUserSessionSafely()
            null
        } catch (e: Exception) {
            // 네트워크 단절 같은 일시적 실패로는 세션을 지우지 않습니다.
            Timber.e("[TokenAuthenticator] 토큰 갱신 실패: ${e.message}")
            null
        }

    private suspend fun currentTokenOrNull(): Token? =
        try {
            userDataUtil.token.first()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.e("[TokenAuthenticator] 토큰 조회 실패: ${e.message}")
            null
        }

    private suspend fun clearUserSessionSafely() {
        try {
            userDataUtil.clearUserSession()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.e("[TokenAuthenticator] 세션 정리 실패: ${e.message}")
        }
    }

    private fun Request.withAuthorization(accessToken: String): Request =
        newBuilder()
            .header(HEADER_AUTHORIZATION, accessToken.convertToToken())
            .build()

    private fun Response.retryCount(): Int {
        var count = 0
        var prior = priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    companion object {
        private const val MAX_RETRY_COUNT = 2
        private val SESSION_EXPIRED_CODES = setOf(401, 403)
    }
}

internal class TokenInterceptor @Inject constructor(
    private val userDataUtil: UserDataUtil,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken =
            runBlocking {
                try {
                    userDataUtil.token.first()?.accessToken ?: ""
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Timber.e("[TokenInterceptor Exception]:${e.message}")
                    ""
                }
            }

        val request: Request =
            chain
                .request()
                .newBuilder()
                .apply { if (accessToken.isNotEmpty()) addHeader(HEADER_AUTHORIZATION, accessToken.convertToToken()) }
                .build()

        return chain.proceed(request)
    }
}

internal const val HEADER_AUTHORIZATION = "Authorization"

fun String?.convertToToken(): String = "Bearer $this"
