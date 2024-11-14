package com.moim.feature.planwrite

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
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
import com.moim.core.designsystem.R
import com.moim.core.model.Meeting
import com.moim.core.model.Member
import com.moim.core.model.asItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
            is PlanWriteUiAction.OnClickBack -> setUiEvent(PlanWriteUiEvent.NavigateToBack)
            is PlanWriteUiAction.OnClickPlanMeeting -> setPlanMeeting(uiAction.meeting)
            is PlanWriteUiAction.OnClickPlanDate -> setPlanDate(uiAction.date)
            is PlanWriteUiAction.OnClickPlanTime -> setPlanTime(uiAction.date)
            is PlanWriteUiAction.OnClickPlanPlace -> {}
            is PlanWriteUiAction.OnClickPlanWrite -> {}
            is PlanWriteUiAction.OnClickRefresh -> onRefresh()
            is PlanWriteUiAction.OnShowDatePickerDialog -> showDatePickerDialog(uiAction.isShow)
            is PlanWriteUiAction.OnShowTimePickerDialog -> showTimePickerDialog(uiAction.isShow)
            is PlanWriteUiAction.OnShowMeetingsDialog -> showMeetingsDialog(uiAction.isShow)
            is PlanWriteUiAction.OnChangePlanName -> setPlanName(uiAction.name)
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
        val meetingDialogUiState: PlanWriteDialogUiState = PlanWriteDialogUiState.Loading,
        val isShowDatePickerDialog: Boolean = false,
        val isShowTimePickerDialog: Boolean = false,
        val isShowMeetingDialog: Boolean = false,
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
    data object OnClickPlanPlace : PlanWriteUiAction
    data object OnClickPlanWrite : PlanWriteUiAction
    data object OnClickRefresh : PlanWriteUiAction
    data class OnClickPlanMeeting(val meeting: Meeting) : PlanWriteUiAction
    data class OnClickPlanDate(val date: ZonedDateTime) : PlanWriteUiAction
    data class OnClickPlanTime(val date: ZonedDateTime) : PlanWriteUiAction

    data class OnShowMeetingsDialog(val isShow: Boolean) : PlanWriteUiAction
    data class OnShowDatePickerDialog(val isShow: Boolean) : PlanWriteUiAction
    data class OnShowTimePickerDialog(val isShow: Boolean) : PlanWriteUiAction
    data class OnChangePlanName(val name: String) : PlanWriteUiAction
}

sealed interface PlanWriteUiEvent : UiEvent {
    data object NavigateToBack : PlanWriteUiEvent
    data class ShowToastMessage(@StringRes val messageRes: Int) : PlanWriteUiEvent
}