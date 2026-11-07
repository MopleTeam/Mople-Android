package com.moim.core.remote.util

import io.ktor.client.plugins.logging.Logger
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import timber.log.Timber

internal class MoimKtorLogger(
    private val json: Json,
) : Logger {
    override fun log(message: String) {
        Timber.tag(TAG).d(message.prettifiedOrSelf())
    }

    // JSON 본문만 담긴 로그는 정렬해서 출력
    private fun String.prettifiedOrSelf(): String {
        val trimmed = trim()
        if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) return this

        return try {
            json.encodeToString(json.parseToJsonElement(trimmed))
        } catch (e: SerializationException) {
            this
        } catch (e: Exception) {
            "Error parsing JSON: ${e.message}, Original message: $this"
        }
    }

    companion object {
        private const val TAG = "Ktor"
    }
}
