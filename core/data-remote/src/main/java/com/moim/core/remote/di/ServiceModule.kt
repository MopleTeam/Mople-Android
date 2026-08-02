package com.moim.core.remote.di

import com.moim.core.remote.di.qualifiers.MoimApi
import com.moim.core.remote.di.qualifiers.NormalApi
import com.moim.core.remote.util.TokenManager
import com.moim.core.remote.util.createMoimHttpClient
import com.moim.core.remote.util.installTokenRefresh
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object ServiceModule {
    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            isLenient = true
            ignoreUnknownKeys = true // 알 수 없는 키 무시
            coerceInputValues = true // 타입 불일치 시 기본값으로 대체
            encodeDefaults = true
            prettyPrint = true
        }

    @MoimApi
    @Singleton
    @Provides
    fun provideMoimHttpClient(
        json: Json,
        tokenManager: TokenManager,
    ): HttpClient = createMoimHttpClient(json).apply { installTokenRefresh(tokenManager) }

    @NormalApi
    @Singleton
    @Provides
    fun provideNormalHttpClient(json: Json): HttpClient = createMoimHttpClient(json)
}
