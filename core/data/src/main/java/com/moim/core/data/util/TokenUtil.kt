package com.moim.core.data.util

import com.moim.core.data.datastore.PreferenceStorage
import com.moim.core.data.service.TokenApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject

internal class TokenAuthenticator @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val tokenApi: TokenApi
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            val refreshToken = preferenceStorage.token.first()?.refreshToken ?: ""
            try {
                val token = tokenApi.getRefreshToken(refreshToken)
                    .also { preferenceStorage.saveUserToken(it) }

                response.request.newBuilder()
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
    private val preferenceStorage: PreferenceStorage
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking {
            try {
                preferenceStorage.token.first()?.accessToken ?: ""
            } catch (e: Exception) {
                Timber.e("[TokenInterceptor Exception]:${e.message}")
                ""
            }
        }

        val request: Request = chain.request()
            .newBuilder()
            .apply { if (accessToken.isNotEmpty()) addHeader("Authorization", accessToken.convertToToken()) }
            .build()

        return chain.proceed(request)
    }
}

internal fun String?.convertToToken(): String {
    return "Bearer $this"
}