package com.moim.feature.profile

import androidx.lifecycle.SavedStateHandle
import com.moim.core.common.view.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
}