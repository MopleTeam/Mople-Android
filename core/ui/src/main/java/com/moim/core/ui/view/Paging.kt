package com.moim.core.ui.view

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import java.time.ZonedDateTime

@Stable
data class PagingUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isLoadingFooter: Boolean = false,
    val isErrorFooter: Boolean = false,
    val isLast: Boolean = false,
    val nextCursor: String? = null,
    val totalCount: Int = 0,
)

@Composable
fun PaginationEffect(
    listState: LazyListState,
    threshold: Int = 3,
    enabled: Boolean = true,
    onNext: () -> Unit,
) {
    val callback by rememberUpdatedState(onNext)
    val shouldLoadNextPage by remember(listState, threshold) {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false
            lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - threshold
        }
    }

    LaunchedEffect(shouldLoadNextPage, enabled) {
        if (shouldLoadNextPage && enabled) {
            callback()
        }
    }
}

const val PAGING_LOADING = "LoadState At Loading"
const val PAGING_ERROR = "LoadState At Error"

inline fun <T : Any> PagingData<T>.checkedActionedAtIsBeforeLoadedAt(
    actionedAt: ZonedDateTime,
    loadedAt: ZonedDateTime,
    function: () -> PagingData<T>,
): PagingData<T> {
    if (actionedAt.isBefore(loadedAt)) {
        return this
    }
    return function()
}

fun CombinedLoadStates.isSuccess(): Boolean = (this.refresh !is LoadState.Loading && this.refresh !is LoadState.Error)

fun CombinedLoadStates.isLoading(): Boolean = (this.refresh is LoadState.Loading)

fun CombinedLoadStates.isError(): Boolean = (this.refresh is LoadState.Error)

fun CombinedLoadStates.isAppendLoading(): Boolean = (this.append is LoadState.Loading)

fun CombinedLoadStates.isAppendError(): Boolean = (this.append is LoadState.Error)
