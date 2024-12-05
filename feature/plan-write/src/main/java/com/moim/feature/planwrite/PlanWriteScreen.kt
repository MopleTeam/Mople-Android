package com.moim.feature.planwrite

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.common.util.getDateFormatLongTime
import com.moim.core.common.util.getDateTimeFormatZoneDate
import com.moim.core.common.view.ObserveAsEvents
import com.moim.core.common.view.showToast
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.component.MoimPrimaryButton
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.planwrite.ui.MoimDatePickerDialog
import com.moim.feature.planwrite.ui.MoimTimePickerDialog
import com.moim.feature.planwrite.ui.PlanWriteMeetingsDialog
import com.moim.feature.planwrite.ui.PlanWriteSelectedBox
import com.moim.feature.planwrite.ui.PlanWriteTextField
import com.moim.feature.planwrite.ui.place.PlaceContainerScreen
import java.time.ZonedDateTime

internal typealias OnPlanWriteUiAction = (PlanWriteUiAction) -> Unit

@Composable
fun PlanWriteRoute(
    viewModel: PlanWriteViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToBack: () -> Unit
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val planWriteUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.white)

    BackHandler {
        viewModel.onUiAction(PlanWriteUiAction.OnClickBack)
    }

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is PlanWriteUiEvent.NavigateToBack -> navigateToBack()
            is PlanWriteUiEvent.ShowToastMessage -> showToast(context, event.messageRes)
        }
    }

    when (val uiState = planWriteUiState) {
        is PlanWriteUiState.PlanWrite -> PlanWriteScreen(
            modifier = modifier,
            uiState = uiState,
            isLoading = isLoading,
            onUiAction = viewModel::onUiAction
        )
    }
}

@Composable
fun PlanWriteScreen(
    modifier: Modifier = Modifier,
    uiState: PlanWriteUiState.PlanWrite,
    isLoading: Boolean = false,
    onUiAction: OnPlanWriteUiAction = {}
) {
    Column(
        modifier = modifier
    ) {
        MoimTopAppbar(
            title = stringResource(if (uiState.planId.isNullOrEmpty()) R.string.plan_write_title_for_create else R.string.plan_write_title_for_update),
            onClickNavigate = { onUiAction(PlanWriteUiAction.OnClickBack) }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            PlanWriteSelectedBox(
                titleText = stringResource(R.string.plan_write_meeting_select),
                hintText = stringResource(R.string.plan_write_meeting_select_hint),
                valueText = uiState.selectMeetingName,
                enable = uiState.enableMeetingSelected,
                onClick = { onUiAction(PlanWriteUiAction.OnShowMeetingsDialog(true)) }
            )
            PlanWriteTextField(
                planName = uiState.planName ?: "",
                onUiAction = onUiAction
            )
            PlanWriteSelectedBox(
                titleText = stringResource(R.string.plan_write_date_select),
                hintText = stringResource(R.string.plan_write_date_select_hint),
                valueText = if (uiState.planDate == null) null else getDateTimeFormatZoneDate(uiState.planDate, stringResource(R.string.regex_date_day)),
                iconRes = R.drawable.ic_calendar,
                onClick = { onUiAction(PlanWriteUiAction.OnShowDatePickerDialog(true)) }
            )
            PlanWriteSelectedBox(
                titleText = stringResource(R.string.plan_write_time_select),
                hintText = stringResource(R.string.plan_write_time_select_hint),
                valueText = if (uiState.planTime == null) null else getDateTimeFormatZoneDate(uiState.planTime, stringResource(R.string.regex_date_time)),
                iconRes = R.drawable.ic_clock,
                onClick = { onUiAction(PlanWriteUiAction.OnShowTimePickerDialog(true)) }
            )
            PlanWriteSelectedBox(
                titleText = stringResource(R.string.plan_write_place_select),
                hintText = stringResource(R.string.plan_write_place_select_hint),
                valueText = uiState.planPlace,
                iconRes = R.drawable.ic_location,
                onClick = { onUiAction(PlanWriteUiAction.OnShowPlaceMapScreen(true)) }
            )

            Spacer(Modifier.weight(1f))

            MoimPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(if (uiState.planId.isNullOrEmpty()) R.string.plan_write_create else R.string.plan_write_update),
                enable = uiState.enabledSubmit,
                onClick = { onUiAction(PlanWriteUiAction.OnClickPlanWrite) }
            )
        }
    }

    if (uiState.isShowMeetingDialog) {
        PlanWriteMeetingsDialog(
            uiState = uiState.meetingDialogUiState,
            onUiAction = onUiAction
        )
    }
    if (uiState.isShowDatePickerDialog) {
        MoimDatePickerDialog(
            date = getDateFormatLongTime(uiState.planDate),
            onDateSelected = { onUiAction(PlanWriteUiAction.OnClickPlanDate(it)) },
            onDismiss = { onUiAction(PlanWriteUiAction.OnShowDatePickerDialog(false)) }
        )
    }
    if (uiState.isShowTimePickerDialog) {
        MoimTimePickerDialog(
            date = uiState.planTime ?: ZonedDateTime.now().withMinute(0),
            onDateSelected = { onUiAction(PlanWriteUiAction.OnClickPlanTime(it)) },
            onDismiss = { onUiAction(PlanWriteUiAction.OnShowTimePickerDialog(false)) }
        )
    }
    if (uiState.isShowMapScreen) {
        PlaceContainerScreen(
            onUiAction = onUiAction,
            searchPlaces = uiState.searchPlaces,
            selectedPlace = uiState.selectedPlace,
            isShowSearchScreen = uiState.isShowMapSearchScreen
        )
    }

    LoadingDialog(isLoading)
}

@Preview
@Composable
private fun PlanWriteScreenPreview() {
    MoimTheme {
        PlanWriteScreen(
            modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.white),
            uiState = PlanWriteUiState.PlanWrite()
        )
    }
}