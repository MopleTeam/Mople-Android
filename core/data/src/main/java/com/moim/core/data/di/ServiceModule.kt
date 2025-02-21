package com.moim.core.data.di

import com.moim.core.data.BuildConfig
import com.moim.core.data.di.qualifiers.MoimApi
import com.moim.core.data.di.qualifiers.MoimApiOkHttp
import com.moim.core.data.di.qualifiers.NormalApi
import com.moim.core.data.di.qualifiers.NormalApiOkHttp
import com.moim.core.data.util.TokenAuthenticator
import com.moim.core.data.util.TokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object ServiceModule {

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor()
            .apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        headerInterceptor: Interceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(headerInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    fun provideHeaderInterceptor(): Interceptor = Interceptor { chain ->
        chain.proceed(chain.addHeaders())
    }

    @Singleton
    @Provides
    fun provideJson(): Json {
        return Json {
            isLenient = true
            coerceInputValues = true
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }

    @MoimApiOkHttp
    @Singleton
    @Provides
    fun provideApiOkHttpCallFactory(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        headerInterceptor: Interceptor,
        tokenInterceptor: TokenInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): Call.Factory = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.MINUTES)
        .readTimeout(10, TimeUnit.MINUTES)
        .writeTimeout(10, TimeUnit.MINUTES)
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor(tokenInterceptor)
        .authenticator(tokenAuthenticator)
        .addInterceptor(headerInterceptor)
        .build()

    @NormalApiOkHttp
    @Singleton
    @Provides
    fun provideNormalApiOkHttpCallFactory(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        headerInterceptor: Interceptor,
    ): Call.Factory = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.MINUTES)
        .readTimeout(10, TimeUnit.MINUTES)
        .writeTimeout(10, TimeUnit.MINUTES)
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor(headerInterceptor)
        .build()

    // header Token이 필요한 API에 사용하는 Retrofit 입니다.
    @MoimApi
    @Singleton
    @Provides
    fun provideRetrofit(
        @MoimApiOkHttp okHttpCallFactory: Call.Factory,
        json: Json,
    ): Retrofit {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .callFactory(okHttpCallFactory)
            .addConverterFactory(json.asConverterFactory(contentType))
            .baseUrl(BuildConfig.API_URL)
            .build()
    }

    // header Token이 필요하지 않은 API에 사용하는 Retrofit 입니다.
    @NormalApi
    @Singleton
    @Provides
    fun provideRetrofitWithoutHeader(
        @NormalApiOkHttp okHttpCallFactory: Call.Factory,
        json: Json,
    ): Retrofit {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .callFactory(okHttpCallFactory)
            .addConverterFactory(json.asConverterFactory(contentType))
            .baseUrl(BuildConfig.API_URL)
            .build()
    }

    private fun Interceptor.Chain.addHeaders(): Request = this.request().newBuilder()
        .addHeader("os", "android")
        .addHeader("version", BuildConfig.VERSION_NAME)
        .build()
}
