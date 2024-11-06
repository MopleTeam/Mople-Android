package com.moim.feature.calendar

import androidx.lifecycle.SavedStateHandle
import com.moim.core.common.view.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

}