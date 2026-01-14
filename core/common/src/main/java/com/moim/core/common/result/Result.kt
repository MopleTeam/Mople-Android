package com.moim.core.common.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

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
        .catch { emit(Result.Error(it as? Exception ?: Exception(it))) }
