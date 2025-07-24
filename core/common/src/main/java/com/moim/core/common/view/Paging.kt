package com.moim.core.common.view

import androidx.paging.PagingData
import java.time.ZonedDateTime

inline fun <T : Any> PagingData<T>.checkedActionedAtIsBeforeLoadedAt(actionedAt: ZonedDateTime, loadedAt: ZonedDateTime, function: () -> PagingData<T>): PagingData<T> {
    if (actionedAt.isBefore(loadedAt)) {
        return this
    }
    return function()
}
