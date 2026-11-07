package com.moim.core.remote.datasource.image

import com.moim.core.remote.di.qualifiers.MoimApi
import com.moim.core.remote.util.decodeJsonString
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.ChannelProvider
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.util.cio.readChannel
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URLEncoder
import javax.inject.Inject

internal class ImageRemoteDataSourceImpl @Inject constructor(
    @MoimApi private val client: HttpClient,
    private val json: Json,
) : ImageRemoteDataSource {
    override suspend fun uploadImage(
        folderName: String,
        file: File,
    ): String =
        client
            .post("image/upload/$folderName") {
                setBody(
                    MultiPartFormDataContent(
                        formData { appendImage(FORM_KEY_IMAGE, file) },
                    ),
                )
            }.decodeJsonString(json)

    override suspend fun uploadReviewImages(
        folderName: String,
        reviewId: String,
        files: List<File>,
    ) {
        client.post("image/review/$folderName") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(FORM_KEY_REVIEW_ID, reviewId)
                        files.forEach { appendImage(FORM_KEY_IMAGES, it) }
                    },
                ),
            )
        }
    }

    // 메모리 적재 대신 채널로 스트리밍
    private fun FormBuilder.appendImage(
        key: String,
        file: File,
    ) {
        append(
            key = key,
            value = ChannelProvider(file.length()) { file.readChannel() },
            headers =
                Headers.build {
                    append(HttpHeaders.ContentType, CONTENT_TYPE_IMAGE)
                    append(
                        HttpHeaders.ContentDisposition,
                        "filename=\"${URLEncoder.encode(file.name, Charsets.UTF_8.displayName())}\"",
                    )
                },
        )
    }

    companion object {
        private const val FORM_KEY_IMAGE = "image"
        private const val FORM_KEY_IMAGES = "images"
        private const val FORM_KEY_REVIEW_ID = "reviewId"
        private const val CONTENT_TYPE_IMAGE = "image/*"
    }
}
