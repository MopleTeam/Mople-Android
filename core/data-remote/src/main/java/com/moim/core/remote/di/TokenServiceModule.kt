package com.moim.core.remote.di

import com.moim.core.remote.BuildConfig
import com.moim.core.remote.di.qualifiers.MoimTokenApiOkHttp
import com.moim.core.remote.service.AuthTokenApi
import com.moim.core.remote.util.TokenAuthenticator
import com.moim.core.remote.util.TokenInterceptor
import com.moim.core.remote.util.UserDataUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object TokenServiceModule {
    @MoimTokenApiOkHttp
    @Singleton
    @Provides
    fun provideApiOkHttpCallFactory(
        headerInterceptor: Interceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): Call.Factory =
        OkHttpClient
            .Builder()
            .connectTimeout(10, TimeUnit.MINUTES)
            .readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)
            .addInterceptor(headerInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Singleton
    @Provides
    fun provideTokenApi(
        @MoimTokenApiOkHttp okHttpCallFactory: Call.Factory,
    ): AuthTokenApi {
        val format =
            Json {
                isLenient = true
                coerceInputValues = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            }
        val contentType = "application/json".toMediaType()

        return Retrofit
            .Builder()
            .callFactory(okHttpCallFactory)
            .addConverterFactory(format.asConverterFactory(contentType))
            .baseUrl(BuildConfig.API_URL)
            .build()
            .create(AuthTokenApi::class.java)
    }

    @Singleton
    @Provides
    fun provideTokenInterceptor(userDataUtil: UserDataUtil): TokenInterceptor = TokenInterceptor(userDataUtil)

    @Singleton
    @Provides
    fun provideTokenAuthenticator(
        userDataUtil: UserDataUtil,
        authTokenApi: AuthTokenApi,
    ): TokenAuthenticator = TokenAuthenticator(userDataUtil, authTokenApi)
}
