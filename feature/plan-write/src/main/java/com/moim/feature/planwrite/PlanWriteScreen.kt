package com.moim.feature.planwrite

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.compose.collectAsLazyPagingItems
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.common.util.parseDateString
import com.moim.core.common.util.parseLongTime
import com.moim.core.designsystem.R
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.component.MoimPrimaryButton
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.view.ObserveAsEvents
import com.moim.core.ui.view.showToast
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
    navigateToBack: () -> Unit,
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val planWriteUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.bg.primary)

    BackHandler {
        viewModel.onUiAction(PlanWriteUiAction.OnClickBack)
    }

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is PlanWriteUiEvent.NavigateToBack -> navigateToBack()
            is PlanWriteUiEvent.ShowToastMessage -> showToast(context, event.message)
        }
    }

    when (val uiState = planWriteUiState) {
        is PlanWriteUiState.PlanWrite -> {
            PlanWriteScreen(
                modifier = modifier,
                uiState = uiState,
                isLoading = isLoading,
                onUiAction = viewModel::onUiAction,
            )
        }
    }
}

@Composable
fun PlanWriteScreen(
    modifier: Modifier = Modifier,
    uiState: PlanWriteUiState.PlanWrite,
    isLoading: Boolean = false,
    onUiAction: OnPlanWriteUiAction = {},
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val meetings = uiState.meetings?.collectAsLazyPagingItems(LocalLifecycleOwner.current.lifecycleScope.coroutineContext)

    TrackScreenViewEvent(screenName = "plan_write")
    MoimScaffold(
        modifier = modifier.imePadding(),
        topBar = {
            MoimTopAppbar(
                title =
                    stringResource(
                        if (uiState.planId.isNullOrEmpty()) R.string.plan_write_title_for_create else R.string.plan_write_title_for_update,
                    ),
                onClickNavigate = { onUiAction(PlanWriteUiAction.OnClickBack) },
            )
        },
        content = {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(it)
                        .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                PlanWriteSelectedBox(
                    title = stringResource(R.string.plan_write_meeting_select),
                    hint = stringResource(R.string.plan_write_meeting_select_hint),
                    value = uiState.selectMeetingName,
                    enable = uiState.enableMeetingSelected,
                    onClick = { onUiAction(PlanWriteUiAction.OnShowMeetingsDialog(true)) },
                )
                PlanWriteTextField(
                    title = stringResource(R.string.plan_write_name),
                    hint = stringResource(R.string.plan_write_name_hint),
                    value = uiState.planName ?: "",
                    onTextChange = { onUiAction(PlanWriteUiAction.OnChangePlanName(it)) },
                )
                PlanWriteSelectedBox(
                    title = stringResource(R.string.plan_write_date_select),
                    hint = stringResource(R.string.plan_write_date_select_hint),
                    value = uiState.planDate?.parseDateString(stringResource(R.string.regex_date_year_month_day)),
                    iconRes = R.drawable.ic_calendar,
                    onClick = { onUiAction(PlanWriteUiAction.OnShowDatePickerDialog(true)) },
                )
                PlanWriteSelectedBox(
                    title = stringResource(R.string.plan_write_time_select),
                    hint = stringResource(R.string.plan_write_time_select_hint),
                    value = uiState.planTime?.parseDateString(stringResource(R.string.regex_date_time)),
                    iconRes = R.drawable.ic_clock,
                    onClick = { onUiAction(PlanWriteUiAction.OnShowTimePickerDialog(true)) },
                )
                PlanWriteSelectedBox(
                    title = stringResource(R.string.plan_write_place_select),
                    titleOption = stringResource(R.string.plan_write_select_option),
                    hint = stringResource(R.string.plan_write_place_select_hint),
                    value = uiState.planLoadAddress,
                    iconRes = R.drawable.ic_location,
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        onUiAction(PlanWriteUiAction.OnShowPlaceMapScreen(true))
                    },
                )
                PlanWriteTextField(
                    modifier = Modifier.defaultMinSize(minHeight = 144.dp),
                    title = stringResource(R.string.plan_write_plan_info),
                    titleOption = stringResource(R.string.plan_write_select_option),
                    hint = stringResource(R.string.plan_write_plan_info_hint),
                    value = uiState.planDescription ?: "",
                    isSingleLine = false,
                    maxLength = 100,
                    onTextChange = { onUiAction(PlanWriteUiAction.OnChangePlanDescription(it)) },
                )
            }
        },
        bottomBar = {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(MoimTheme.colors.bg.primary)
                        .padding(20.dp),
            ) {
                MoimPrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(if (uiState.planId.isNullOrEmpty()) R.string.plan_write_create else R.string.plan_write_update),
                    enable = uiState.enabledSubmit,
                    onClick = { onUiAction(PlanWriteUiAction.OnClickPlanWrite) },
                )
            }
        },
    )

    if (uiState.isShowMeetingDialog && meetings != null) {
        PlanWriteMeetingsDialog(
            meetings = meetings,
            onUiAction = onUiAction,
        )
    }
    if (uiState.isShowDatePickerDialog) {
        MoimDatePickerDialog(
            date = uiState.planDate.parseLongTime(),
            onDateSelected = { onUiAction(PlanWriteUiAction.OnClickPlanDate(it)) },
            onDismiss = { onUiAction(PlanWriteUiAction.OnShowDatePickerDialog(false)) },
        )
    }
    if (uiState.isShowTimePickerDialog) {
        MoimTimePickerDialog(
            date = uiState.planTime ?: ZonedDateTime.now().plusHours(1).withMinute(0),
            onDateSelected = { onUiAction(PlanWriteUiAction.OnClickPlanTime(it)) },
            onDismiss = { onUiAction(PlanWriteUiAction.OnShowTimePickerDialog(false)) },
        )
    }
    if (uiState.isShowMapScreen) {
        PlaceContainerScreen(
            onUiAction = onUiAction,
            searchKeyword = uiState.searchKeyword,
            searchPlaces = uiState.searchPlaces,
            selectedPlace = uiState.selectedPlace,
            planLongitude = uiState.planLongitude,
            planLatitude = uiState.planLatitude,
            isShowSearchScreen = uiState.isShowMapSearchScreen,
            isShowPlaceInfoDialog = uiState.isShowPlaceInfoDialog,
        )
    }

    LoadingDialog(isLoading)
}

@ThemePreviews
@Composable
private fun PlanWriteScreenPreview() {
    MoimTheme {
        PlanWriteScreen(
            modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.bg.primary),
            uiState = PlanWriteUiState.PlanWrite(),
        )
    }
}
