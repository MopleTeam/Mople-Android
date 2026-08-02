package com.moim.core.remote.util

import com.moim.core.common.exception.BadRequestException
import com.moim.core.common.exception.ConflictException
import com.moim.core.common.exception.ForbiddenException
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.exception.NotFoundException
import com.moim.core.common.exception.ServerErrorException
import com.moim.core.common.exception.UnAuthorizedException
import com.moim.core.common.exception.UnknownErrorException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException

@Serializable
data class ErrorResponse(
    @SerialName("code")
    val code: String? = null,
    @SerialName("message")
    val message: String? = null,
)

// 실패 응답 본문을 파싱해 도메인 예외로 변환한다.
internal suspend fun HttpResponse.toNetworkException(json: Json): NetworkException {
    val errorBody = runCatching { json.decodeFromString(ErrorResponse.serializer(), bodyAsText()) }.getOrNull()
    val code = errorBody?.code?.toIntOrNull() ?: status.value
    val message = errorBody?.message ?: status.description

    return when (code) {
        400 -> BadRequestException(message, null)
        401 -> UnAuthorizedException(message, null)
        403 -> ForbiddenException(message, null)
        404 -> NotFoundException(message, null)
        409 -> ConflictException(message, null)
        500 -> ServerErrorException(message, null)
        else -> UnknownErrorException(message, null)
    }
}

// 연결 실패·타임아웃·직렬화 오류처럼 응답 검증 밖에서 터진 예외를 변환한다.
internal fun Throwable.toNetworkException(): Throwable =
    when (this) {
        is CancellationException -> this
        is NetworkException -> this
        is IOException -> this
        else -> UnknownErrorException(message, this)
    }
