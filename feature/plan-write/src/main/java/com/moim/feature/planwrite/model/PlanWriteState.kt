package com.moim.feature.planwrite.model

import androidx.compose.runtime.Immutable
import com.moim.core.common.model.Place
import com.moim.core.ui.view.PagingUiState
import java.time.ZonedDateTime

@Immutable
data class PlanWriteState(
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
    val meetings: List<MeetingUiModel> = emptyList(),
    val meetingsPagingInfo: PagingUiState = PagingUiState(),
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
)
