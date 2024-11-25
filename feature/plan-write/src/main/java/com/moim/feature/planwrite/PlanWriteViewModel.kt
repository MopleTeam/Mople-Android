package com.moim.feature.planwrite

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.util.parseZoneDateTime
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.common.view.checkState
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.model.MeetingResponse
import com.moim.core.data.model.PlaceResponse
import com.moim.core.designsystem.R
import com.moim.core.model.Meeting
import com.moim.core.model.Place
import com.moim.core.model.asItem
import com.moim.core.route.DetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okio.IOException
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class PlanWriteViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val planRepository: PlanRepository,
    private val meetingRepository: MeetingRepository,
) : BaseViewModel() {

    private val plan
        get() = savedStateHandle.toRoute<DetailRoute.PlanWrite>(DetailRoute.PlanWrite.typeMap).plan

    init {
        viewModelScope.launch {
            plan?.let { plan ->
                val planDate = plan.planTime.parseZoneDateTime()

                setUiState(
                    PlanWriteUiState.PlanWrite(
                        planId = plan.planId,
                        planName = plan.planName,
                        planDate = planDate,
                        planTime = planDate,
                        planPlace = plan.planAddress,
                        planLongitude = plan.planLongitude,
                        planLatitude = plan.planLatitude,
                        selectMeetingId = plan.meetingId,
                        selectMeetingName = plan.meetingName,
                        enableMeetingSelected = false
                    )
                )
            } ?: run { setUiState(PlanWriteUiState.PlanWrite()) }
        }
    }

    fun onUiAction(uiAction: PlanWriteUiAction) {
        when (uiAction) {
            is PlanWriteUiAction.OnClickBack -> navigateToBack()
            is PlanWriteUiAction.OnClickPlanMeeting -> setPlanMeeting(uiAction.meeting)
            is PlanWriteUiAction.OnClickPlanDate -> setPlanDate(uiAction.date)
            is PlanWriteUiAction.OnClickPlanTime -> setPlanTime(uiAction.date)
            is PlanWriteUiAction.OnClickPlanPlaceSearch -> getSearchPlace(uiAction.keyword, uiAction.xPoint, uiAction.yPoint)
            is PlanWriteUiAction.OnClickPlanPlace -> setPlaceMarker(uiAction.place)
            is PlanWriteUiAction.OnClickPlanWrite -> {}
            is PlanWriteUiAction.OnClickRefresh -> onRefresh()
            is PlanWriteUiAction.OnShowDatePickerDialog -> showDatePickerDialog(uiAction.isShow)
            is PlanWriteUiAction.OnShowTimePickerDialog -> showTimePickerDialog(uiAction.isShow)
            is PlanWriteUiAction.OnShowMeetingsDialog -> showMeetingsDialog(uiAction.isShow)
            is PlanWriteUiAction.OnShowPlaceMapScreen -> showPlaceMapScreen(uiAction.isShow)
            is PlanWriteUiAction.OnChangePlanName -> setPlanName(uiAction.name)
        }
    }

    private fun setPlaceMarker(place: Place) {
        uiState.checkState<PlanWriteUiState.PlanWrite> {
            setUiState(
                copy(
                    planPlace = place.title,
                    selectedPlace = place,
                    isShowMapSearchScreen = false
                )
            )
        }
    }

    private fun setPlanName(name: String) {
        uiState.checkState<PlanWriteUiState.PlanWrite> {
            setUiState(copy(planName = name))
        }
    }

    private fun setPlanDate(date: ZonedDateTime) {
        uiState.checkState<PlanWriteUiState.PlanWrite> {
            setUiState(copy(planDate = date))
        }
    }

    private fun setPlanTime(date: ZonedDateTime) {
        uiState.checkState<PlanWriteUiState.PlanWrite> {
            setUiState(copy(planTime = date))
        }
    }

    private fun setPlanMeeting(meeting: Meeting) {
        uiState.checkState<PlanWriteUiState.PlanWrite> {
            val dialogState = if (meetingDialogUiState is PlanWriteDialogUiState.Success) {
                meetingDialogUiState.copy(selectedMeeting = meeting)
            } else {
                meetingDialogUiState
            }

            setUiState(
                copy(
                    selectMeetingId = meeting.id,
                    selectMeetingName = meeting.name,
                    meetingDialogUiState = dialogState
                )
            )
        }
    }

    private fun getSearchPlace(keyword: String, x: String, y: String) {
        viewModelScope.launch {
            uiState.checkState<PlanWriteUiState.PlanWrite> {
                val trimKeyword = keyword.trim()

                if (trimKeyword == searchKeyword) return@launch setUiState(copy(isShowMapSearchScreen = true))

                planRepository.getSearchPlace(trimKeyword, x, y)
                    .asResult()
                    .onEach { setLoading(it is Result.Loading) }
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> return@collect
                            is Result.Success -> setUiState(
                                copy(
                                    searchKeyword = trimKeyword,
                                    isShowMapSearchScreen = true,
                                    searchPlaces = result.data
                                        .map(PlaceResponse::asItem)
                                        .distinctBy { it.roadAddress }
                                )
                            )

                            is Result.Error -> when (result.exception) {
                                is IOException -> setUiEvent(PlanWriteUiEvent.ShowToastMessage(R.string.common_error_disconnection))
                                is NetworkException -> setUiEvent(PlanWriteUiEvent.ShowToastMessage(R.string.common_error_disconnection))
                            }
                        }
                    }
            }
        }
    }

    private fun showDatePickerDialog(isShow: Boolean) {
        uiState.checkState<PlanWriteUiState.PlanWrite> {
            setUiState(copy(isShowDatePickerDialog = isShow))
        }
    }

    private fun showTimePickerDialog(isShow: Boolean) {
        uiState.checkState<PlanWriteUiState.PlanWrite> {
            setUiState(copy(isShowTimePickerDialog = isShow))
        }
    }

    private fun showPlaceMapScreen(isShow: Boolean) {
        uiState.checkState<PlanWriteUiState.PlanWrite> {
            setUiState(copy(isShowMapScreen = isShow))
        }
    }

    private fun showMeetingsDialog(isShow: Boolean) {
        viewModelScope.launch {
            uiState.checkState<PlanWriteUiState.PlanWrite> {
                if (isShow && meetingDialogUiState !is PlanWriteDialogUiState.Success) {
                    meetingRepository.getMeetings()
                        .asResult()
                        .collect { result ->
                            when (result) {
                                is Result.Loading -> setUiState(copy(isShowMeetingDialog = true, meetingDialogUiState = PlanWriteDialogUiState.Loading))
                                is Result.Success -> {
                                    val meetings = result.data.map(MeetingResponse::asItem)

                                    setUiState(
                                        copy(
                                            isShowMeetingDialog = true,
                                            meetingDialogUiState = PlanWriteDialogUiState.Success(
                                                meetings = meetings,
                                                selectedMeeting = meetings.find { selectMeetingId == it.id }
                                            )
                                        )
                                    )
                                }

                                is Result.Error -> {
                                    setUiState(copy(isShowMeetingDialog = false))
                                    setUiEvent(PlanWriteUiEvent.ShowToastMessage(R.string.plan_write_meeting_select_error))
                                }
                            }
                        }
                } else {
                    setUiState(copy(isShowMeetingDialog = isShow))
                }
            }
        }
    }

    private fun navigateToBack() {
        if (uiState.value !is PlanWriteUiState.PlanWrite) {
            return setUiEvent(PlanWriteUiEvent.NavigateToBack)
        }

        uiState.checkState<PlanWriteUiState.PlanWrite> {
            if (isShowMapSearchScreen) {
                setUiState(copy(isShowMapSearchScreen = false))
            } else if (isShowMapScreen) {
                setUiState(copy(isShowMapScreen = false, searchPlaces = emptyList(), searchKeyword = null))
            } else {
                setUiEvent(PlanWriteUiEvent.NavigateToBack)
            }
        }
    }
}

sealed interface PlanWriteUiState : UiState {
    data class PlanWrite(
        val planId: String? = null,
        val planName: String? = null,
        val planDate: ZonedDateTime? = null,
        val planTime: ZonedDateTime? = null,
        val planPlace: String? = null,
        val planLongitude: Double = 0.0,
        val planLatitude: Double = 0.0,
        val selectMeetingId: String? = null,
        val selectMeetingName: String? = null,
        val selectedPlace: Place? = null,
        val meetingDialogUiState: PlanWriteDialogUiState = PlanWriteDialogUiState.Loading,
        val searchKeyword: String? = null,
        val searchPlaces: List<Place> = emptyList(),
        val isShowDatePickerDialog: Boolean = false,
        val isShowTimePickerDialog: Boolean = false,
        val isShowMeetingDialog: Boolean = false,
        val isShowMapScreen: Boolean = false,
        val isShowMapSearchScreen: Boolean = false,
        val enableMeetingSelected: Boolean = true,
        val enabledSubmit: Boolean = false,
    ) : PlanWriteUiState
}

sealed interface PlanWriteDialogUiState : UiState {
    data object Loading : PlanWriteDialogUiState
    data class Success(
        val meetings: List<Meeting>,
        val selectedMeeting: Meeting? = null,
    ) : PlanWriteDialogUiState
}

sealed interface PlanWriteUiAction : UiAction {
    data object OnClickBack : PlanWriteUiAction
    data object OnClickPlanWrite : PlanWriteUiAction
    data object OnClickRefresh : PlanWriteUiAction
    data class OnClickPlanPlaceSearch(val keyword: String, val xPoint: String, val yPoint: String) : PlanWriteUiAction
    data class OnClickPlanPlace(val place: Place) : PlanWriteUiAction
    data class OnClickPlanMeeting(val meeting: Meeting) : PlanWriteUiAction
    data class OnClickPlanDate(val date: ZonedDateTime) : PlanWriteUiAction
    data class OnClickPlanTime(val date: ZonedDateTime) : PlanWriteUiAction

    data class OnShowMeetingsDialog(val isShow: Boolean) : PlanWriteUiAction
    data class OnShowDatePickerDialog(val isShow: Boolean) : PlanWriteUiAction
    data class OnShowTimePickerDialog(val isShow: Boolean) : PlanWriteUiAction
    data class OnShowPlaceMapScreen(val isShow: Boolean) : PlanWriteUiAction
    data class OnChangePlanName(val name: String) : PlanWriteUiAction
}

sealed interface PlanWriteUiEvent : UiEvent {
    data object NavigateToBack : PlanWriteUiEvent
    data class ShowToastMessage(@StringRes val messageRes: Int) : PlanWriteUiEvent
}