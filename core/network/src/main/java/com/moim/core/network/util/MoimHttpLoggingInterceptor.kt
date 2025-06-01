package com.moim.core.network.util

import com.moim.core.network.BuildConfig
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

internal class MoimHttpLoggingInterceptor(
    private val json: Json,
) : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        if (!message.startsWith("{") && !message.startsWith("[")) {
            if (message.startsWith("-->") || message.startsWith("<--")) {
                Timber.tag("OkHttp").d(message)
            }
            return
        }
        try {
            val jsonElement: JsonElement = json.parseToJsonElement(message)
            Timber.tag("OkHttp").d(json.encodeToString(jsonElement))
        } catch (e: SerializationException) {
            Timber.tag("OkHttp").d(message) // Log the original message on parsing failure
        } catch (e: Exception) {
            // Catch any other exceptions during parsing
            Timber.tag("OkHttp").d("Error parsing JSON: ${e.message}, Original message: $message")
        }
    }

    val interceptor: HttpLoggingInterceptor
        get() {
            val interceptor = HttpLoggingInterceptor(this)
            val level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            interceptor.setLevel(level)
            return interceptor
        }
}
