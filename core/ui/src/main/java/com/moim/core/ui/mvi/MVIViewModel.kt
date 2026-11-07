package com.moim.core.ui.mvi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.orbitmvi.orbit.OrbitContainer
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.orbitContainer
import kotlin.time.Duration.Companion.minutes

abstract class MVIViewModel<STATE : Any, SIDE_EFFECT : Any>(
    initialState: STATE,
) : ViewModel(),
    OrbitContainerHost<STATE, STATE, SIDE_EFFECT> {
    // buildSettings가 즉시 실행되므로 container보다 먼저 선언되어야 함
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception -> onError(exception) }

    override val container: OrbitContainer<STATE, STATE, SIDE_EFFECT> =
        orbitContainer(
            initialState = initialState,
            buildSettings = {
                exceptionHandler = coroutineExceptionHandler
                repeatOnSubscribedStopTimeout = 10.minutes.inWholeMilliseconds
            },
            onCreate = {
                onContainerCreate()
            },
        )

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    protected fun setLoading(isLoading: Boolean) {
        _loading.update { isLoading }
    }

    protected open suspend fun Syntax<STATE, SIDE_EFFECT>.onContainerCreate() {
        // no-op
    }

    open fun onIntent(intent: Intent) = Unit

    protected open fun onError(throwable: Throwable) = Unit
}
