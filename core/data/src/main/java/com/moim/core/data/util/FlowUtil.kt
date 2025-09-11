package com.moim.core.data.util

import com.moim.core.remote.util.converterException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

fun <T> catchFlow(block: suspend FlowCollector<T>.() -> Unit): Flow<T> =
    SafeFlow(block)
        .catch {
            Timber.e("[CatchFlow Exception]=$it")
            throw converterException(it)
        }
        .flowOn(Dispatchers.IO)

private class SafeFlow<T>(
    private val block: suspend FlowCollector<T>.() -> Unit,
) : AbstractFlow<T>() {
    override suspend fun collectSafely(collector: FlowCollector<T>) {
        collector.block()
    }
}
