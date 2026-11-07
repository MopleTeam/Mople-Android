package com.moim.core.remote.util

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

internal suspend fun HttpClient.postJson(
    urlString: String,
    params: JsonObject? = null,
    block: HttpRequestBuilder.() -> Unit = {},
): HttpResponse =
    post(urlString) {
        jsonBody(params)
        block()
    }

internal suspend fun HttpClient.patchJson(
    urlString: String,
    params: JsonObject? = null,
    block: HttpRequestBuilder.() -> Unit = {},
): HttpResponse =
    patch(urlString) {
        jsonBody(params)
        block()
    }

internal suspend fun HttpClient.putJson(
    urlString: String,
    params: JsonObject? = null,
    block: HttpRequestBuilder.() -> Unit = {},
): HttpResponse =
    put(urlString) {
        jsonBody(params)
        block()
    }

internal suspend fun HttpClient.deleteJson(
    urlString: String,
    params: JsonObject? = null,
    block: HttpRequestBuilder.() -> Unit = {},
): HttpResponse =
    delete(urlString) {
        jsonBody(params)
        block()
    }

// body<String>()은 원문을 그대로 주므로 따옴표를 직접 벗긴다.
internal suspend fun HttpResponse.decodeJsonString(json: Json): String = json.decodeFromString(bodyAsText())

private fun HttpRequestBuilder.jsonBody(params: JsonObject?) {
    if (params == null) return

    contentType(ContentType.Application.Json)
    setBody(params)
}
