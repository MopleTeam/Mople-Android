package com.moim.feature.planwrite.model

import com.moim.core.common.model.Meeting
import com.moim.core.common.model.Place
import com.moim.core.ui.mvi.Intent
import java.time.ZonedDateTime

sealed interface PlanWriteIntent : Intent {
    data object BackClick : PlanWriteIntent

    data object PlanWriteClick : PlanWriteIntent

    data object NextMeetingsPageLoad : PlanWriteIntent

    data class PlanPlaceSearchClick(
        val keyword: String,
        val xPoint: String,
        val yPoint: String,
    ) : PlanWriteIntent

    data class SearchPlaceClick(
        val place: Place,
    ) : PlanWriteIntent

    data class PlanPlaceClick(
        val place: Place,
    ) : PlanWriteIntent

    data class PlanMeetingClick(
        val meeting: Meeting,
    ) : PlanWriteIntent

    data class PlanDateSelect(
        val date: ZonedDateTime,
    ) : PlanWriteIntent

    data class PlanTimeSelect(
        val date: ZonedDateTime,
    ) : PlanWriteIntent

    data class MeetingsDialogShow(
        val isShow: Boolean,
    ) : PlanWriteIntent

    data class DatePickerDialogShow(
        val isShow: Boolean,
    ) : PlanWriteIntent

    data class TimePickerDialogShow(
        val isShow: Boolean,
    ) : PlanWriteIntent

    data class PlaceInfoDialogShow(
        val isShow: Boolean,
    ) : PlanWriteIntent

    data class PlaceMapScreenShow(
        val isShow: Boolean,
    ) : PlanWriteIntent

    data class PlanNameChange(
        val name: String,
    ) : PlanWriteIntent

    data class PlanDescriptionChange(
        val description: String,
    ) : PlanWriteIntent
}
