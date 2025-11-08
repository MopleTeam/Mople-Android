package com.moim.core.ui.eventbus

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventBus<T : EventAction> @Inject constructor() {
    private val _action =
        MutableSharedFlow<T>(
            extraBufferCapacity = Int.MAX_VALUE,
            onBufferOverflow = BufferOverflow.DROP_LATEST,
        )
    val action: SharedFlow<T> = _action

    fun send(action: T) = _action.tryEmit(action)
}
