package com.moim.core.remote.di

import com.moim.core.remote.di.qualifiers.TokenRefreshApi
import com.moim.core.remote.util.createMoimHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object TokenServiceModule {
    @TokenRefreshApi
    @Singleton
    @Provides
    fun provideTokenRefreshHttpClient(json: Json): HttpClient =
        createMoimHttpClient(
            json = json,
            connectTimeoutMillis = TIMEOUT_CONNECT_MILLIS,
            socketTimeoutMillis = TIMEOUT_SOCKET_MILLIS,
        )

    // 원본 요청을 대기시키므로 일반 API보다 짧게
    private const val TIMEOUT_CONNECT_MILLIS = 10_000L
    private const val TIMEOUT_SOCKET_MILLIS = 15_000L
}
