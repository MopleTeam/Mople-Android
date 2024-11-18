package com.moim.feature.planwrite

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
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
import com.moim.core.model.Member
import com.moim.core.model.Place
import com.moim.core.model.asItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
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

    private val planId
        get() = savedStateHandle.get<String>(KEY_PLAN_ID)

    private val planResult = combine(loadDataSignal, flowOf(planId), ::Pair)
        .filter { (_, id) -> id != null }
        .flatMapLatest { (_, id) -> planRepository.getPlan(id!!).asResult() }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            if (planId == null) {
                setUiState(PlanWriteUiState.Success())
            } else {
                planResult.collect { result ->
                    when (result) {
                        is Result.Loading -> setUiState(PlanWriteUiState.Loading)
                        is Result.Success -> {
                            val plan = result.data.asItem()
                            val planDate = plan.startedAt.parseZoneDateTime()

                            setUiState(
                                PlanWriteUiState.Success(
                                    planId = plan.id,
                                    planName = plan.name,
                                    planDate = planDate,
                                    planTime = planDate,
                                    planPlace = plan.address,
                                    planLongitude = plan.longitude,
                                    planLatitude = plan.latitude,
                                    selectMeetingId = plan.meetingId,
                                    selectMeetingName = plan.meetingName,
                                )
                            )
                        }

                        is Result.Error -> setUiState(PlanWriteUiState.Error)
                    }
                }
            }
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
        uiState.checkState<PlanWriteUiState.Success> {
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
        uiState.checkState<PlanWriteUiState.Success> {
            setUiState(copy(planName = name))
        }
    }

    private fun setPlanDate(date: ZonedDateTime) {
        uiState.checkState<PlanWriteUiState.Success> {
            setUiState(copy(planDate = date))
        }
    }

    private fun setPlanTime(date: ZonedDateTime) {
        uiState.checkState<PlanWriteUiState.Success> {
            setUiState(copy(planTime = date))
        }
    }

    private fun setPlanMeeting(meeting: Meeting) {
        uiState.checkState<PlanWriteUiState.Success> {
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
            uiState.checkState<PlanWriteUiState.Success> {
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
        uiState.checkState<PlanWriteUiState.Success> {
            setUiState(copy(isShowDatePickerDialog = isShow))
        }
    }

    private fun showTimePickerDialog(isShow: Boolean) {
        uiState.checkState<PlanWriteUiState.Success> {
            setUiState(copy(isShowTimePickerDialog = isShow))
        }
    }

    private fun showPlaceMapScreen(isShow: Boolean) {
        uiState.checkState<PlanWriteUiState.Success> {
            setUiState(copy(isShowMapScreen = isShow))
        }
    }

    private fun showMeetingsDialog(isShow: Boolean) {
        viewModelScope.launch {
            uiState.checkState<PlanWriteUiState.Success> {
                if (isShow && meetingDialogUiState !is PlanWriteDialogUiState.Success) {
                    meetingRepository.getMeetings()
                        .asResult()
                        .collect { result ->
                            when (result) {
                                is Result.Loading -> setUiState(copy(isShowMeetingDialog = true, meetingDialogUiState = PlanWriteDialogUiState.Loading))
                                is Result.Success -> {
                                    val meetings = result.data.map(MeetingResponse::asItem) + sampleMeeting
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
        if (uiState.value !is PlanWriteUiState.Success) {
            return setUiEvent(PlanWriteUiEvent.NavigateToBack)
        }

        uiState.checkState<PlanWriteUiState.Success> {
            if (isShowMapSearchScreen) {
                setUiState(copy(isShowMapSearchScreen = false))
            } else if (isShowMapScreen) {
                setUiState(copy(isShowMapScreen = false, searchPlaces = emptyList(), searchKeyword = null))
            } else {
                setUiEvent(PlanWriteUiEvent.NavigateToBack)
            }
        }
    }

    companion object {
        private const val KEY_PLAN_ID = "planId"

        private val sampleMeeting = listOf(
            Meeting(
                id = "1",
                name = "우리중학교 동창1",
                imageUrl = "https://plus.unsplash.com/premium_photo-1698507574126-7135d2684aa2",
                members = listOf(Member(), Member(), Member()),
            ),
            Meeting(
                id = "2",
                name = "우리중학교 동창2",
                imageUrl = "https://images.unsplash.com/photo-1730829807497-9c5b8c9c41c4",
            ),
            Meeting(
                id = "3",
                name = "우리중학교 동창3",
                imageUrl = "https://images.unsplash.com/photo-1730812393789-a7d15960029d",
            ),
            Meeting(
                id = "4",
                name = "우리중학교 동창4",
                imageUrl = "https://plus.unsplash.com/premium_photo-1670333183316-ab697ddd9b13",
            ),
        )
    }
}

sealed interface PlanWriteUiState : UiState {
    data object Loading : PlanWriteUiState

    data class Success(
        val planId: String? = null,
        val planName: String? = null,
        val planDate: ZonedDateTime? = null,
        val planTime: ZonedDateTime? = null,
        val planPlace: String? = null,
        val planLongitude: Long = 0,
        val planLatitude: Long = 0,
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
        val enabled: Boolean = false,
    ) : PlanWriteUiState

    data object Error : PlanWriteUiState
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