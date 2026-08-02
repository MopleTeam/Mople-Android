package com.moim.core.remote.util

import com.moim.core.remote.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// 토큰 부착·갱신은 별도로 [installTokenRefresh]를 적용한다.
internal fun createMoimHttpClient(
    json: Json,
    connectTimeoutMillis: Long = TIMEOUT_CONNECT_MILLIS,
    socketTimeoutMillis: Long = TIMEOUT_SOCKET_MILLIS,
): HttpClient =
    HttpClient(CIO) {
        // 실패 응답은 아래 validator에서 MoimHttpException으로 변환
        expectSuccess = false

        install(ContentNegotiation) {
            json(json)
        }

        install(Logging) {
            logger = MoimKtorLogger(json)
            level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE
        }

        install(HttpTimeout) {
            this.connectTimeoutMillis = connectTimeoutMillis
            // I/O 1회 기준이라 대용량 업로드도 충분
            this.socketTimeoutMillis = socketTimeoutMillis
        }

        defaultRequest {
            url(BuildConfig.API_URL)
            header(HEADER_OS, OS_ANDROID)
            header(HEADER_VERSION, BuildConfig.VERSION_NAME)
        }

        HttpResponseValidator {
            validateResponse { response ->
                if (response.status.isSuccess()) return@validateResponse

                throw MoimHttpException(
                    statusCode = response.status.value,
                    statusMessage = response.status.description,
                    errorBody = runCatching { response.bodyAsText() }.getOrDefault(""),
                )
            }
        }
    }

private const val HEADER_OS = "os"
private const val HEADER_VERSION = "version"
private const val OS_ANDROID = "android"

internal const val TIMEOUT_CONNECT_MILLIS = 15_000L
internal const val TIMEOUT_SOCKET_MILLIS = 30_000L
