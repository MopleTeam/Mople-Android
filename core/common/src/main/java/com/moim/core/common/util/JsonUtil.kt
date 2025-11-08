package com.moim.core.common.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

object JsonUtil {

    val json = Json {
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