package com.moim.core.common.result

import com.moim.core.common.exception.NetworkException
import com.moim.core.common.exception.UnknownErrorException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.io.IOException

sealed interface Result<out R> {
    data object Loading : Result<Nothing>

    data class Success<out T>(
        val data: T,
    ) : Result<T>

    data class Error(
        val exception: Exception,
    ) : Result<Nothing>
}

val Result<*>.isSuccess
    get() = this is Result.Success && data != null

val <T> Result<T>.data: T?
    get() = (this as? Result.Success)?.data

fun <T> Flow<T>.asResult(): Flow<Result<T>> =
    this
        .map<T, Result<T>> { Result.Success(it) }
        .onStart { emit(Result.Loading) }
        .catch { emit(Result.Error(it.asHandledException())) }

private fun Throwable.asHandledException(): Exception =
    when (this) {
        is IOException -> this
        is NetworkException -> this
        else -> UnknownErrorException(message, this)
    }
