package com.moim.core.data.util

import com.moim.core.common.exception.BadRequestException
import com.moim.core.common.exception.ConflictException
import com.moim.core.common.exception.ForbiddenException
import com.moim.core.common.exception.NotFoundException
import com.moim.core.common.exception.ServerErrorException
import com.moim.core.common.exception.UnAuthorizedException
import com.moim.core.common.exception.UnknownErrorException
import retrofit2.HttpException
import java.io.IOException

internal fun converterException(exception: Exception): Exception {
    return when (exception) {
        is IOException -> exception
        is HttpException -> {
            val code = (exception.response()?.code())
            val message = exception.response()?.message()
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

        else -> UnknownErrorException(exception.message, exception)
    }
}