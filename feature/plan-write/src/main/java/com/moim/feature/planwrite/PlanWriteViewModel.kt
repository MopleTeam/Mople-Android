package com.moim.feature.planwrite

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.model.Meeting
import com.moim.core.common.model.Place
import com.moim.core.common.model.item.asPlanItem
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.util.parseDateString
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.domain.usecase.GetMeetingsUseCase
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.PlanAction
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.ToastMessage
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import com.moim.feature.planwrite.model.MeetingUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException
import java.time.ZonedDateTime

@HiltViewModel(assistedFactory = PlanWriteViewModel.Factory::class)
class PlanWriteViewModel @AssistedInject constructor(
    private val planRepository: PlanRepository,
    getMeetingsUseCase: GetMeetingsUseCase,
    private val planEventBus: EventBus<PlanAction>,
    @Assisted val planWriteRoute: DetailRoute.PlanWrite,
) : BaseViewModel() {
    private val planItem = planWriteRoute.planItem
    private val selectedMeetingId = MutableStateFlow<String?>(null)

    private val meetings =
        getMeetingsUseCase()
            .mapLatest { it.map { meeting -> MeetingUiModel(meeting) } }
            .cachedIn(viewModelScope)
            .combine(selectedMeetingId) { meetings, selectedMeetingId ->
                meetings.map { pagingData ->
                    pagingData.copy(isSelected = pagingData.meeting.id == selectedMeetingId)
                }
            }.cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            planItem?.let { plan ->
                setUiState(
                    PlanWriteUiState.PlanWrite(
                        planId = plan.postId,
                        planName = plan.planName,
                        planDate = plan.planAt,
                        planTime = plan.planAt,
                        planLoadAddress = plan.loadAddress,
                        planWeatherAddress = plan.weatherAddress,
                        planPlaceName = plan.planName,
                        planDescription = plan.description,
                        planLongitude = plan.longitude,
                        planLatitude = plan.latitude,
                        selectMeetingId = plan.meetingId,
                        selectMeetingName = plan.meetingName,
                        enableMeetingSelected = false,
                        enabledSubmit = plan.postId.isNotEmpty(),
                    ),
                )
                selectedMeetingId.update { plan.meetingId }
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
            is PlanWriteUiAction.OnClickSearchPlace -> setPlaceMarker(uiAction.place)
            is PlanWriteUiAction.OnClickPlanPlace -> setPlanPlace(uiAction.place)
            is PlanWriteUiAction.OnClickPlanWrite -> setPlan()
            is PlanWriteUiAction.OnShowDatePickerDialog -> showDatePickerDialog(uiAction.isShow)
            is PlanWriteUiAction.OnShowTimePickerDialog -> showTimePickerDialog(uiAction.isShow)
            is PlanWriteUiAction.OnShowMeetingsDialog -> showMeetingsDialog(uiAction.isShow)
            is PlanWriteUiAction.OnShowPlaceMapScreen -> showPlaceMapScreen(uiAction.isShow)
            is PlanWriteUiAction.OnShowPlaceInfoDialog -> showPlaceInfoDialog(uiAction.isShow)
            is PlanWriteUiAction.OnChangePlanName -> setPlanName(uiAction.name)
            is PlanWriteUiAction.OnChangePlanDescription -> setPlanDescription(uiAction.description)
        }
    }

    private fun setPlaceMarker(place: Place) {
        uiState.checkState<PlanWriteUiState.PlanWrite> {
            setUiState(
                copy(
                    selectedPlace = place,
                    planLongitude = place.xPoint.toDouble(),
                    planLatitude = place.yPoint.toDouble(),
                    isShowPlaceInfoDialog = true,
                    isShowMapSearchScreen = false,
                ),
            )
        }
    }

    private fun setPlanPlace(place: Place) {
        uiState.checkState<PlanWriteUiState.PlanWrite> {
            setUiState(
                copy(
                    planLoadAddress = place.roadAddress,
                    planWeatherAddress = place.address,
                    planPlaceName = place.title,
                    planLongitude = place.xPoint.toDouble(),
                    planLatitude = place.yPoint.toDouble(),
                    selectedPlace = null,
                    isShowMapScreen = false,
                ),
            )
        }
    }

    private fun setPlanName(name: String) {
        uiState.checkState<PlanWriteUiState.PlanWrite> {
            setUiState(copy(planName = name))
            setPlanCreateEnabled()
        }
    }

    private fun setPlanDescription(planInfo: String) {
        uiState.checkState<PlanWriteUiState.PlanWrite> {
            setUiState(copy(planDescription = planInfo))
            setPlanCreateEnabled()
        }
    }

    private fun setPlanDate(date: ZonedDateTime) {
        uiState.checkState<PlanWriteUiState.PlanWrite> {
            setUiState(copy(planDate = date))
            setPlanCreateEnabled()
        }
    }

    private fun setPlanTime(date: ZonedDateTime) {
        uiState.checkState<PlanWriteUiState.PlanWrite> {
            setUiState(copy(planTime = date))
            setPlanCreateEnabled()
        }
    }

    private fun setPlanMeeting(meeting: Meeting) {
        uiState.checkState<PlanWriteUiState.PlanWrite> {
            selectedMeetingId.update { meeting.id }
            setUiState(copy(selectMeetingId = meeting.id, selectMeetingName = meeting.name))
            setPlanCreateEnabled()
        }
    }

    private fun setPlanCreateEnabled() {
        uiState.checkState<PlanWriteUiState.PlanWrite> {
            val enable =
                planName.isNullOrEmpty().not() &&
                    selectMeetingId.isNullOrEmpty().not() &&
                    planDate != null &&
                    planTime != null

            setUiState(copy(enabledSubmit = enable))
        }
    }

    private fun getSearchPlace(
        keyword: String,
        x: String,
        y: String,
    ) {
        viewModelScope.launch {
            uiState.checkState<PlanWriteUiState.PlanWrite> {
                val trimKeyword = keyword.trim()

                if (trimKeyword == searchKeyword) return@launch setUiState(copy(isShowMapSearchScreen = true))

                planRepository
                    .getSearchPlace(trimKeyword, x, y)
                    .asResult()
                    .onEach { setLoading(it is Result.Loading) }
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> {
                                return@collect
                            }

                            is Result.Success -> {
                                setUiState(
                                    copy(
                                        searchKeyword = trimKeyword,
                                        isShowMapSearchScreen = true,
                                        searchPlaces = result.data.filter { it.roadAddress.isNotEmpty() }.distinctBy { it.roadAddress },
                                    ),
                                )
                            }

                            is Result.Error -> {
                                when (result.exception) {
                                    is IOException -> setUiEvent(PlanWriteUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
                                    is NetworkException -> setUiEvent(PlanWriteUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun setPlan() {
        viewModelScope.launch {
            uiState.checkState<PlanWriteUiState.PlanWrite> {
                val planTime = requireNotNull(planDate).withHour(requireNotNull(planTime).hour).withMinute(planTime.minute)

                if (requireNotNull(planTime).isBefore(ZonedDateTime.now())) {
                    setUiEvent(PlanWriteUiEvent.ShowToastMessage(ToastMessage.PlanWriteTimeErrorMessage))
                    return@launch
                }

                if (planId.isNullOrEmpty()) {
                    planRepository
                        .createPlan(
                            meetingId = requireNotNull(selectMeetingId),
                            planName = requireNotNull(planName),
                            planTime = planTime.parseDateString(),
                            planAddress = planLoadAddress,
                            planWeatherAddress = planWeatherAddress,
                            planDescription = planDescription,
                            title = planPlaceName ?: "",
                            longitude = planLongitude,
                            latitude = planLatitude,
                        )
                } else {
                    planRepository
                        .updatePlan(
                            planId = planId,
                            planName = requireNotNull(planName),
                            planTime = planTime.parseDateString(),
                            planAddress = planLoadAddress,
                            planWeatherAddress = planWeatherAddress,
                            planDescription = planDescription,
                            title = planPlaceName ?: "",
                            longitude = planLongitude,
                            latitude = planLatitude,
                        )
                }.asResult().onEach { setLoading(it is Result.Loading) }.collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            return@collect
                        }

                        is Result.Success -> {
                            if (planId.isNullOrEmpty()) {
                                planEventBus.send(PlanAction.PlanCreate(planItem = result.data.asPlanItem()))
                            } else {
                                planEventBus.send(PlanAction.PlanUpdate(planItem = result.data.asPlanItem()))
                            }
                            setUiEvent(PlanWriteUiEvent.NavigateToBack)
                        }

                        is Result.Error -> {
                            when (result.exception) {
                                is IOException -> setUiEvent(PlanWriteUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
                                is NetworkException -> setUiEvent(PlanWriteUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
                            }
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

    private fun showPlaceInfoDialog(isShow: Boolean) {
        uiState.checkState<PlanWriteUiState.PlanWrite> {
            setUiState(copy(isShowPlaceInfoDialog = isShow))
        }
    }

    private fun showPlaceMapScreen(isShow: Boolean) {
        uiState.checkState<PlanWriteUiState.PlanWrite> {
            setUiState(
                copy(
                    isShowMapScreen = isShow,
                    isShowMapSearchScreen = isShow,
                    searchKeyword = null,
                    selectedPlace = null,
                    searchPlaces = emptyList(),
                ),
            )
        }
    }

    private fun showMeetingsDialog(isShow: Boolean) {
        viewModelScope.launch {
            uiState.checkState<PlanWriteUiState.PlanWrite> {
                setUiState(
                    copy(
                        isShowMeetingDialog = isShow,
                        meetings = if (isShow) this@PlanWriteViewModel.meetings else null,
                    ),
                )
            }
        }
    }

    private fun navigateToBack() {
        if (uiState.value !is PlanWriteUiState.PlanWrite) {
            return setUiEvent(PlanWriteUiEvent.NavigateToBack)
        }

        uiState.checkState<PlanWriteUiState.PlanWrite> {
            if (isShowMapScreen) {
                showPlaceMapScreen(false)
            } else {
                setUiEvent(PlanWriteUiEvent.NavigateToBack)
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(planWriteRoute: DetailRoute.PlanWrite): PlanWriteViewModel
    }
}

sealed interface PlanWriteUiState : UiState {
    data class PlanWrite(
        val planId: String? = null,
        val planName: String? = null,
        val planDescription: String? = null,
        val planDate: ZonedDateTime? = null,
        val planTime: ZonedDateTime? = null,
        val planLoadAddress: String? = null,
        val planWeatherAddress: String? = null,
        val planPlaceName: String? = null,
        val planLongitude: Double? = null,
        val planLatitude: Double? = null,
        val selectMeetingId: String? = null,
        val selectMeetingName: String? = null,
        val selectedPlace: Place? = null,
        val meetings: Flow<PagingData<MeetingUiModel>>? = null,
        val searchKeyword: String? = null,
        val searchPlaces: List<Place> = emptyList(),
        val isShowDatePickerDialog: Boolean = false,
        val isShowTimePickerDialog: Boolean = false,
        val isShowMeetingDialog: Boolean = false,
        val isShowPlaceInfoDialog: Boolean = true,
        val isShowMapScreen: Boolean = false,
        val isShowMapSearchScreen: Boolean = true,
        val enableMeetingSelected: Boolean = true,
        val enabledSubmit: Boolean = false,
    ) : PlanWriteUiState
}

sealed interface PlanWriteUiAction : UiAction {
    data object OnClickBack : PlanWriteUiAction

    data object OnClickPlanWrite : PlanWriteUiAction

    data class OnClickPlanPlaceSearch(
        val keyword: String,
        val xPoint: String,
        val yPoint: String,
    ) : PlanWriteUiAction

    data class OnClickSearchPlace(
        val place: Place,
    ) : PlanWriteUiAction

    data class OnClickPlanPlace(
        val place: Place,
    ) : PlanWriteUiAction

    data class OnClickPlanMeeting(
        val meeting: Meeting,
    ) : PlanWriteUiAction

    data class OnClickPlanDate(
        val date: ZonedDateTime,
    ) : PlanWriteUiAction

    data class OnClickPlanTime(
        val date: ZonedDateTime,
    ) : PlanWriteUiAction

    data class OnShowMeetingsDialog(
        val isShow: Boolean,
    ) : PlanWriteUiAction

    data class OnShowDatePickerDialog(
        val isShow: Boolean,
    ) : PlanWriteUiAction

    data class OnShowTimePickerDialog(
        val isShow: Boolean,
    ) : PlanWriteUiAction

    data class OnShowPlaceInfoDialog(
        val isShow: Boolean,
    ) : PlanWriteUiAction

    data class OnShowPlaceMapScreen(
        val isShow: Boolean,
    ) : PlanWriteUiAction

    data class OnChangePlanName(
        val name: String,
    ) : PlanWriteUiAction

    data class OnChangePlanDescription(
        val description: String,
    ) : PlanWriteUiAction
}

sealed interface PlanWriteUiEvent : UiEvent {
    data object NavigateToBack : PlanWriteUiEvent

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : PlanWriteUiEvent
}
