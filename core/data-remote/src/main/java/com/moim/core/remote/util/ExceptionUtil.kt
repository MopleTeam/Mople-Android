package com.moim.core.remote.util

import com.moim.core.common.exception.BadRequestException
import com.moim.core.common.exception.ConflictException
import com.moim.core.common.exception.ForbiddenException
import com.moim.core.common.exception.NotFoundException
import com.moim.core.common.exception.ServerErrorException
import com.moim.core.common.exception.UnAuthorizedException
import com.moim.core.common.exception.UnknownErrorException
import com.moim.core.common.util.JsonUtil.toObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.HttpException
import java.io.IOException

@Serializable
data class ErrorResponse(
    @SerialName("code")
    val code: String? = null,
    @SerialName("message")
    val message: String? = null,
)

fun converterException(exception: Throwable): Exception {
    return when (exception) {
        is IOException -> {
            exception
        }

        is HttpException -> {
            val errorBody = (exception.response()?.errorBody()?.string() ?: "").toObject<ErrorResponse>()
            val code = errorBody?.code?.toInt() ?: (exception.response()?.code())
            val message = errorBody?.message ?: (exception.response()?.message())
            val cause = exception.cause

            return when (code) {
                400 -> BadRequestException(message, cause)
                401 -> UnAuthorizedException(message, cause)
                403 -> ForbiddenException(message, cause)
                404 -> NotFoundException(message, cause)
                409 -> ConflictException(message, cause)
                500 -> ServerErrorException(message, cause)
                else -> UnknownErrorException(message, cause)
            }
        }

        else -> {
            UnknownErrorException(exception.message, exception)
        }
    }
}
