package com.moim.core.common.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data object None : UiState

open class BaseViewModel : ViewModel() {

    private val refreshSignal = MutableSharedFlow<Unit>()

    protected val loadDataSignal: Flow<Unit> = flow {
        emit(Unit)
        emitAll(refreshSignal)
    }

    protected fun onRefresh() = viewModelScope.launch {
        refreshSignal.emit(Unit)
    }

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _uiState = MutableStateFlow<UiState>(None)
    val uiState: StateFlow<UiState> = _uiState

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    protected fun setLoading(isLoading: Boolean) {
        _loading.value = isLoading
    }

    protected fun setUiState(uiState: UiState) {
        _uiState.update { uiState }
    }

    protected fun setUiEvent(uiEvent: UiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(uiEvent)
        }
    }
}