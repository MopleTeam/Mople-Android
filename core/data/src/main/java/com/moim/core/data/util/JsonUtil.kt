package com.moim.core.data.util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import timber.log.Timber

internal object JsonUtil {

    private val json = Json {
        isLenient = true
        coerceInputValues = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    inline fun <reified T> T.toJson(): String {
        return json.encodeToString(this)
    }

    inline fun <reified T> String.toObject(): T? {
        return try {
            json.decodeFromString<T>(this)
        } catch (error: Exception) {
            Timber.e("[JsonUtil Exception]:${error.message}")
            Timber.e("[JsonUtil Exception Content]:${this}")
            throw error
        }
    }

    fun jsonOf(vararg pairs: Pair<String, *>): JsonObject {
        val map = mutableMapOf<String, JsonElement>()
        pairs.toMap().forEach { it.key.let { key -> map[key] = it.value.toJsonElement() } }
        return JsonObject(map)
    }

    private fun Any?.toJsonElement(): JsonElement {
        return when (this) {
            is Number -> JsonPrimitive(this)
            is Boolean -> JsonPrimitive(this)
            is String -> JsonPrimitive(this)
            is Array<*> -> this.toJsonArray()
            is List<*> -> this.toJsonArray()
            is Map<*, *> -> this.toJsonObject()
            is JsonElement -> this
            else -> JsonNull
        }
    }

    private fun Array<*>.toJsonArray(): JsonArray {
        val array = mutableListOf<JsonElement>()
        this.forEach { array.add(it.toJsonElement()) }
        return JsonArray(array)
    }

    private fun List<*>.toJsonArray(): JsonArray {
        val array = mutableListOf<JsonElement>()
        this.forEach { array.add(it.toJsonElement()) }
        return JsonArray(array)
    }

    private fun Map<*, *>.toJsonObject(): JsonObject {
        val map = mutableMapOf<String, JsonElement>()
        this.forEach {
            (it.key as? String)?.let { key -> map[key] = it.value.toJsonElement() }
        }
        return JsonObject(map)
    }

}