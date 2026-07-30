package com.moim.core.remote.di

import com.moim.core.remote.BuildConfig
import com.moim.core.remote.di.qualifiers.MoimApi
import com.moim.core.remote.di.qualifiers.MoimApiOkHttp
import com.moim.core.remote.di.qualifiers.NormalApi
import com.moim.core.remote.di.qualifiers.NormalApiOkHttp
import com.moim.core.remote.util.MoimHttpLoggingInterceptor
import com.moim.core.remote.util.TokenAuthenticator
import com.moim.core.remote.util.TokenInterceptor
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

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(json: Json): HttpLoggingInterceptor = MoimHttpLoggingInterceptor(json).interceptor

    @Provides
    fun provideHeaderInterceptor(): Interceptor =
        Interceptor { chain ->
            chain.proceed(chain.addHeaders())
        }

    @MoimApiOkHttp
    @Singleton
    @Provides
    fun provideApiOkHttpCallFactory(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        headerInterceptor: Interceptor,
        tokenInterceptor: TokenInterceptor,
        tokenAuthenticator: TokenAuthenticator,
    ): Call.Factory =
        OkHttpClient
            .Builder()
            .connectTimeout(TIMEOUT_CONNECT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_READ_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_WRITE_SECONDS, TimeUnit.SECONDS)
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
    ): Call.Factory =
        OkHttpClient
            .Builder()
            .connectTimeout(TIMEOUT_CONNECT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_READ_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_WRITE_SECONDS, TimeUnit.SECONDS)
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

        return Retrofit
            .Builder()
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

        return Retrofit
            .Builder()
            .callFactory(okHttpCallFactory)
            .addConverterFactory(json.asConverterFactory(contentType))
            .baseUrl(BuildConfig.API_URL)
            .build()
    }

    private fun Interceptor.Chain.addHeaders(): Request =
        this
            .request()
            .newBuilder()
            .addHeader("os", "android")
            .addHeader("version", BuildConfig.VERSION_NAME)
            .build()

    // OkHttp의 read/write 타임아웃은 전체 전송 시간이 아닌 소켓 I/O 1회 기준이므로
    // 이미지 업로드 같은 대용량 요청도 아래 값으로 충분합니다.
    private const val TIMEOUT_CONNECT_SECONDS = 15L
    private const val TIMEOUT_READ_SECONDS = 30L
    private const val TIMEOUT_WRITE_SECONDS = 30L
}
