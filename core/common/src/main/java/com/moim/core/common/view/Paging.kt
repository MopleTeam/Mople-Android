package com.moim.core.common.view

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import java.time.ZonedDateTime

const val PAGING_LOADING = "LoadState At Loading"
const val PAGING_ERROR = "LoadState At Error"

inline fun <T : Any> PagingData<T>.checkedActionedAtIsBeforeLoadedAt(actionedAt: ZonedDateTime, loadedAt: ZonedDateTime, function: () -> PagingData<T>): PagingData<T> {
    if (actionedAt.isBefore(loadedAt)) {
        return this
    }
    return function()
}

fun CombinedLoadStates.isSuccess(): Boolean {
    return (this.refresh !is LoadState.Loading && this.refresh !is LoadState.Error)
}

fun CombinedLoadStates.isLoading(): Boolean {
    return (this.refresh is LoadState.Loading)
}

fun CombinedLoadStates.isError(): Boolean {
    return (this.refresh is LoadState.Error)
}

fun CombinedLoadStates.isAppendLoading(): Boolean {
    return (this.append is LoadState.Loading)
}

fun CombinedLoadStates.isAppendError(): Boolean {
    return (this.append is LoadState.Error)
}