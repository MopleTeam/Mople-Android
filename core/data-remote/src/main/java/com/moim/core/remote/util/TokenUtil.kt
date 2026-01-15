package com.moim.core.remote.util

import com.moim.core.remote.model.asItem
import com.moim.core.remote.service.AuthTokenApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
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
    ): Request? =
        runBlocking {
            mutex.withLock {
                val refreshToken = userDataUtil.token.first()?.refreshToken ?: ""

                try {
                    val token =
                        authTokenApi
                            .getRefreshToken(refreshToken)
                            .also { userDataUtil.saveUserToken(it.asItem()) }

                    response.request
                        .newBuilder()
                        .header("Authorization", token.accessToken.convertToToken())
                        .build()
                } catch (e: Exception) {
                    Timber.e("[TokenAuthenticator Exception]:${e.message}")
                    null
                }
            }
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
                } catch (e: Exception) {
                    Timber.e("[TokenInterceptor Exception]:${e.message}")
                    ""
                }
            }

        val request: Request =
            chain
                .request()
                .newBuilder()
                .apply { if (accessToken.isNotEmpty()) addHeader("Authorization", accessToken.convertToToken()) }
                .build()

        return chain.proceed(request)
    }
}

fun String?.convertToToken(): String = "Bearer $this"
