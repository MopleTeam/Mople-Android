package com.moim.feature.planwrite

import com.moim.core.common.model.Meeting
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.Place
import com.moim.core.common.model.item.asPlanItem
import com.moim.core.common.util.parseDateString
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.PlanAction
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.util.isActiveCheck
import com.moim.core.ui.view.PagingHelper
import com.moim.core.ui.view.ToastMessage
import com.moim.feature.planwrite.model.MeetingUiModel
import com.moim.feature.planwrite.model.PlanWriteIntent
import com.moim.feature.planwrite.model.PlanWriteSideEffect
import com.moim.feature.planwrite.model.PlanWriteState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import org.orbitmvi.orbit.syntax.Syntax
import java.io.IOException
import java.time.ZonedDateTime

@HiltViewModel(assistedFactory = PlanWriteViewModel.Factory::class)
class PlanWriteViewModel @AssistedInject constructor(
    private val planRepository: PlanRepository,
    private val meetingRepository: MeetingRepository,
    private val planEventBus: EventBus<PlanAction>,
    @Assisted val planWriteRoute: DetailRoute.PlanWrite,
) : MVIViewModel<PlanWriteState, PlanWriteSideEffect>(planWriteRoute.asState()) {
    private var meetingsPagingJob: Job? = null

    override fun onIntent(intent: Intent) {
        if (intent !is PlanWriteIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is PlanWriteIntent.BackClick -> {
                    navigateToBack()
                }

                is PlanWriteIntent.PlanWriteClick -> {
                    setPlan()
                }

                is PlanWriteIntent.NextMeetingsPageLoad -> {
                    getMeetings(state.meetingsPagingInfo.nextCursor)
                }

                is PlanWriteIntent.PlanMeetingClick -> {
                    setPlanMeeting(intent.meeting)
                }

                is PlanWriteIntent.PlanDateSelect -> {
                    reduce { state.copy(planDate = intent.date) }
                    setPlanCreateEnabled()
                }

                is PlanWriteIntent.PlanTimeSelect -> {
                    reduce { state.copy(planTime = intent.date) }
                    setPlanCreateEnabled()
                }

                is PlanWriteIntent.PlanPlaceSearchClick -> {
                    getSearchPlace(intent.keyword, intent.xPoint, intent.yPoint)
                }

                is PlanWriteIntent.SearchPlaceClick -> {
                    setPlaceMarker(intent.place)
                }

                is PlanWriteIntent.PlanPlaceClick -> {
                    setPlanPlace(intent.place)
                }

                is PlanWriteIntent.DatePickerDialogShow -> {
                    reduce { state.copy(isShowDatePickerDialog = intent.isShow) }
                }

                is PlanWriteIntent.TimePickerDialogShow -> {
                    reduce { state.copy(isShowTimePickerDialog = intent.isShow) }
                }

                is PlanWriteIntent.PlaceInfoDialogShow -> {
                    reduce { state.copy(isShowPlaceInfoDialog = intent.isShow) }
                }

                is PlanWriteIntent.PlaceMapScreenShow -> {
                    showPlaceMapScreen(intent.isShow)
                }

                is PlanWriteIntent.MeetingsDialogShow -> {
                    showMeetingsDialog(intent.isShow)
                }

                is PlanWriteIntent.PlanNameChange -> {
                    reduce { state.copy(planName = intent.name) }
                    setPlanCreateEnabled()
                }

                is PlanWriteIntent.PlanDescriptionChange -> {
                    reduce { state.copy(planDescription = intent.description) }
                    setPlanCreateEnabled()
                }
            }
        }
    }

    private suspend fun Syntax<PlanWriteState, PlanWriteSideEffect>.setPlaceMarker(place: Place) {
        reduce {
            state.copy(
                selectedPlace = place,
                planLongitude = place.xPoint.toDouble(),
                planLatitude = place.yPoint.toDouble(),
                isShowPlaceInfoDialog = true,
                isShowMapSearchScreen = false,
            )
        }
    }

    private suspend fun Syntax<PlanWriteState, PlanWriteSideEffect>.setPlanPlace(place: Place) {
        reduce {
            state.copy(
                planLoadAddress = place.roadAddress,
                planWeatherAddress = place.address,
                planPlaceName = place.title,
                planLongitude = place.xPoint.toDouble(),
                planLatitude = place.yPoint.toDouble(),
                selectedPlace = null,
                isShowMapScreen = false,
            )
        }
    }

    private suspend fun Syntax<PlanWriteState, PlanWriteSideEffect>.setPlanMeeting(meeting: Meeting) {
        val updatedMeetings = state.meetings.map { it.copy(isSelected = it.meeting.id == meeting.id) }

        reduce {
            state.copy(
                selectMeetingId = meeting.id,
                selectMeetingName = meeting.name,
                meetings = updatedMeetings,
            )
        }

        setPlanCreateEnabled()
    }

    private suspend fun Syntax<PlanWriteState, PlanWriteSideEffect>.setPlanCreateEnabled() {
        val enable =
            state.planName.isNullOrEmpty().not() &&
                state.selectMeetingId.isNullOrEmpty().not() &&
                state.planDate != null &&
                state.planTime != null

        reduce { state.copy(enabledSubmit = enable) }
    }

    private fun getMeetings(cursor: String? = null) {
        if (meetingsPagingJob.isActiveCheck()) return
        meetingsPagingJob =
            intent {
                handleMeetingsPagingData(
                    pagingData = null,
                    isLoading = true,
                    cursor = cursor,
                )

                val pagingData =
                    runCatching {
                        meetingRepository.getMeetings(
                            cursor = cursor ?: "",
                            size = 30,
                        )
                    }.getOrNull()

                handleMeetingsPagingData(
                    pagingData = pagingData,
                    isLoading = false,
                    cursor = cursor,
                )
            }
    }

    private suspend fun Syntax<PlanWriteState, PlanWriteSideEffect>.handleMeetingsPagingData(
        pagingData: PaginationContainer<List<Meeting>>?,
        isLoading: Boolean,
        cursor: String?,
    ) {
        val selectedId = state.selectMeetingId
        val result =
            PagingHelper.handlePagingResult(
                pagingData = pagingData,
                isLoading = isLoading,
                currentPagingInfo = state.meetingsPagingInfo,
                currentItems = state.meetings,
                isInitialLoad = cursor == null,
                transform = { items ->
                    items.map { meeting ->
                        MeetingUiModel(meeting = meeting, isSelected = meeting.id == selectedId)
                    }
                },
            )

        reduce {
            state.copy(
                meetingsPagingInfo = result.pagingInfo,
                meetings = result.items,
            )
        }
    }

    private suspend fun Syntax<PlanWriteState, PlanWriteSideEffect>.getSearchPlace(
        keyword: String,
        x: String,
        y: String,
    ) {
        val trimKeyword = keyword.trim()

        // 같은 키워드면 재검색 없이 검색 화면만 다시 띄운다.
        if (trimKeyword == state.searchKeyword) {
            reduce { state.copy(isShowMapSearchScreen = true) }
            return
        }

        setLoading(true)

        try {
            val places =
                planRepository.getSearchPlace(
                    keyword = trimKeyword,
                    xPoint = x,
                    yPoint = y,
                )

            reduce {
                state.copy(
                    searchKeyword = trimKeyword,
                    isShowMapSearchScreen = true,
                    searchPlaces = places.filter { it.roadAddress.isNotEmpty() }.distinctBy { it.roadAddress },
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<PlanWriteState, PlanWriteSideEffect>.setPlan() {
        val planId = state.planId
        val selectedDate = requireNotNull(state.planDate)
        val selectedTime = requireNotNull(state.planTime)
        val planTime = selectedDate.withHour(selectedTime.hour).withMinute(selectedTime.minute)

        if (planTime.isBefore(ZonedDateTime.now())) {
            postSideEffect(PlanWriteSideEffect.ShowToastMessage(ToastMessage.PlanWriteTimeErrorMessage))
            return
        }

        setLoading(true)

        try {
            val plan =
                if (planId.isNullOrEmpty()) {
                    planRepository.createPlan(
                        meetingId = requireNotNull(state.selectMeetingId),
                        planName = requireNotNull(state.planName),
                        planTime = planTime.parseDateString(),
                        planAddress = state.planLoadAddress,
                        planWeatherAddress = state.planWeatherAddress,
                        planDescription = state.planDescription,
                        title = state.planPlaceName ?: "",
                        longitude = state.planLongitude,
                        latitude = state.planLatitude,
                    )
                } else {
                    planRepository.updatePlan(
                        planId = planId,
                        planName = requireNotNull(state.planName),
                        planTime = planTime.parseDateString(),
                        planAddress = state.planLoadAddress,
                        planWeatherAddress = state.planWeatherAddress,
                        planDescription = state.planDescription,
                        title = state.planPlaceName ?: "",
                        longitude = state.planLongitude,
                        latitude = state.planLatitude,
                    )
                }

            if (planId.isNullOrEmpty()) {
                planEventBus.send(PlanAction.PlanCreate(planItem = plan.asPlanItem()))
            } else {
                planEventBus.send(PlanAction.PlanUpdate(planItem = plan.asPlanItem()))
            }

            postSideEffect(PlanWriteSideEffect.NavigateToBack)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<PlanWriteState, PlanWriteSideEffect>.showPlaceMapScreen(isShow: Boolean) {
        reduce {
            state.copy(
                isShowMapScreen = isShow,
                isShowMapSearchScreen = isShow,
                searchKeyword = null,
                selectedPlace = null,
                searchPlaces = emptyList(),
            )
        }
    }

    private suspend fun Syntax<PlanWriteState, PlanWriteSideEffect>.showMeetingsDialog(isShow: Boolean) {
        reduce { state.copy(isShowMeetingDialog = isShow) }

        if (isShow && state.meetings.isEmpty()) {
            getMeetings()
        }
    }

    private suspend fun Syntax<PlanWriteState, PlanWriteSideEffect>.navigateToBack() {
        if (state.isShowMapScreen) {
            showPlaceMapScreen(false)
        } else {
            postSideEffect(PlanWriteSideEffect.NavigateToBack)
        }
    }

    private suspend fun Syntax<PlanWriteState, PlanWriteSideEffect>.showErrorToast(exception: Throwable) {
        val message = if (exception is IOException) ToastMessage.NetworkErrorMessage else ToastMessage.ServerErrorMessage
        postSideEffect(PlanWriteSideEffect.ShowToastMessage(message))
    }

    @AssistedFactory
    interface Factory {
        fun create(planWriteRoute: DetailRoute.PlanWrite): PlanWriteViewModel
    }
}

private fun DetailRoute.PlanWrite.asState() =
    planItem?.let { plan ->
        PlanWriteState(
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
        )
    } ?: PlanWriteState()
