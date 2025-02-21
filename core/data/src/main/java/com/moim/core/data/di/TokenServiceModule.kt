package com.moim.core.data.di

import com.moim.core.data.BuildConfig
import com.moim.core.data.datastore.PreferenceStorage
import com.moim.core.data.di.qualifiers.MoimTokenApiOkHttp
import com.moim.core.data.util.TokenAuthenticator
import com.moim.core.data.util.TokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import com.moim.core.data.service.AuthTokenApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType

@InstallIn(SingletonComponent::class)
@Module
internal object TokenServiceModule {

    @MoimTokenApiOkHttp
    @Singleton
    @Provides
    fun provideApiOkHttpCallFactory(
        headerInterceptor: Interceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): Call.Factory = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.MINUTES)
        .readTimeout(10, TimeUnit.MINUTES)
        .writeTimeout(10, TimeUnit.MINUTES)
        .addInterceptor(headerInterceptor)
        .addInterceptor(httpLoggingInterceptor)
        .build()

    @Singleton
    @Provides
    fun provideTokenApi(
        @MoimTokenApiOkHttp okHttpCallFactory: Call.Factory
    ): AuthTokenApi {
        val format = Json {
            isLenient = true
            coerceInputValues = true
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .callFactory(okHttpCallFactory)
            .addConverterFactory(format.asConverterFactory(contentType))
            .baseUrl(BuildConfig.API_URL)
            .build()
            .create(AuthTokenApi::class.java)
    }

    @Singleton
    @Provides
    fun provideTokenInterceptor(
        preferenceStorage: PreferenceStorage
    ): TokenInterceptor = TokenInterceptor(preferenceStorage)

    @Singleton
    @Provides
    fun provideTokenAuthenticator(
        preferenceStorage: PreferenceStorage,
        authTokenApi: AuthTokenApi
    ): TokenAuthenticator = TokenAuthenticator(preferenceStorage, authTokenApi)
}