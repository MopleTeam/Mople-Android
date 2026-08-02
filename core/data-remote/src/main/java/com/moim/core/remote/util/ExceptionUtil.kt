package com.moim.core.remote.util

import com.moim.core.common.exception.BadRequestException
import com.moim.core.common.exception.ConflictException
import com.moim.core.common.exception.ForbiddenException
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.exception.NotFoundException
import com.moim.core.common.exception.ServerErrorException
import com.moim.core.common.exception.UnAuthorizedException
import com.moim.core.common.exception.UnknownErrorException
import com.moim.core.common.util.JsonUtil.toObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.IOException

@Serializable
data class ErrorResponse(
    @SerialName("code")
    val code: String? = null,
    @SerialName("message")
    val message: String? = null,
)

// 응답 본문은 suspend로만 읽히므로 검증 단계에서 미리 읽어 담아둔다.
internal class MoimHttpException(
    val statusCode: Int,
    val statusMessage: String,
    val errorBody: String,
) : RuntimeException("HTTP $statusCode $statusMessage: $errorBody")

fun converterException(exception: Throwable): Exception =
    when (exception) {
        // 이미 변환된 예외
        is NetworkException -> exception

        is MoimHttpException -> {
            val errorBody = runCatching { exception.errorBody.toObject<ErrorResponse>() }.getOrNull()
            val code = errorBody?.code?.toIntOrNull() ?: exception.statusCode
            val message = errorBody?.message ?: exception.statusMessage

            when (code) {
                400 -> BadRequestException(message, exception)
                401 -> UnAuthorizedException(message, exception)
                403 -> ForbiddenException(message, exception)
                404 -> NotFoundException(message, exception)
                409 -> ConflictException(message, exception)
                500 -> ServerErrorException(message, exception)
                else -> UnknownErrorException(message, exception)
            }
        }

        // 연결 실패·타임아웃
        is IOException -> exception

        else -> UnknownErrorException(exception.message, exception)
    }
