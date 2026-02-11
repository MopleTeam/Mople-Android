package com.moim.core.ui.view

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.moim.core.common.model.PaginationContainer

object PagingHelper {
    fun <T, R> handlePagingResult(
        pagingData: PaginationContainer<List<T>>?,
        isLoading: Boolean,
        currentPagingInfo: PagingUiState,
        currentItems: List<R>,
        isInitialLoad: Boolean,
        transform: (List<T>) -> List<R>,
    ): PagingResult<R> =
        if (isInitialLoad) {
            initializePaging(pagingData, isLoading, transform)
        } else {
            addLoadPaging(pagingData, isLoading, currentPagingInfo, currentItems, transform)
        }

    private fun <T, R> initializePaging(
        pagingData: PaginationContainer<List<T>>?,
        isLoading: Boolean,
        transform: (List<T>) -> List<R>,
    ): PagingResult<R> =
        when {
            isLoading -> {
                PagingResult(
                    pagingInfo = PagingUiState(isLoading = true),
                    items = emptyList(),
                )
            }

            pagingData == null -> {
                PagingResult(
                    pagingInfo = PagingUiState(isLoading = false, isError = true),
                    items = emptyList(),
                )
            }

            else -> {
                val data = transform(pagingData.content)
                PagingResult(
                    pagingInfo =
                        PagingUiState(
                            isLoading = false,
                            nextCursor = pagingData.page.nextCursor,
                            isLast = !pagingData.page.isNext || data.isEmpty(),
                            totalCount = pagingData.totalCount,
                        ),
                    items = data,
                )
            }
        }

    private fun <T, R> addLoadPaging(
        pagingData: PaginationContainer<List<T>>?,
        isLoading: Boolean,
        currentPagingInfo: PagingUiState,
        currentItems: List<R>,
        transform: (List<T>) -> List<R>,
    ): PagingResult<R> =
        when {
            isLoading -> {
                PagingResult(
                    pagingInfo =
                        currentPagingInfo.copy(
                            isLoadingFooter = true,
                            isErrorFooter = false,
                        ),
                    items = currentItems,
                )
            }

            pagingData == null -> {
                PagingResult(
                    pagingInfo =
                        currentPagingInfo.copy(
                            isLoadingFooter = false,
                            isErrorFooter = true,
                        ),
                    items = currentItems,
                )
            }

            else -> {
                val addData = transform(pagingData.content)
                PagingResult(
                    pagingInfo =
                        currentPagingInfo.copy(
                            isLoadingFooter = false,
                            isErrorFooter = false,
                            nextCursor = pagingData.page.nextCursor,
                            isLast = !pagingData.page.isNext || addData.isEmpty(),
                            totalCount = pagingData.totalCount,
                        ),
                    items = currentItems + addData,
                )
            }
        }
}

data class PagingResult<R>(
    val pagingInfo: PagingUiState,
    val items: List<R>,
)

@Stable
data class PagingUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isLoadingFooter: Boolean = false,
    val isErrorFooter: Boolean = false,
    val isLast: Boolean = false,
    val nextCursor: String? = null,
    val totalCount: Int = 0,
) {
    val isSuccess: Boolean
        get() = !isLoading && !isError
}

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
