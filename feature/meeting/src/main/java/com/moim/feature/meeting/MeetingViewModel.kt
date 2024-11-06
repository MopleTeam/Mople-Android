package com.moim.feature.meeting

import androidx.lifecycle.SavedStateHandle
import com.moim.core.common.view.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MeetingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
}