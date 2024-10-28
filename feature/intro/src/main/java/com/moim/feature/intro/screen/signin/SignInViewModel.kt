package com.moim.feature.intro.screen.signin

import androidx.lifecycle.SavedStateHandle
import com.moim.core.common.view.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

}