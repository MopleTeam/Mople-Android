package com.moim.feature.alarmsetting

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.designsystem.R
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.component.MoimSwitch
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.view.ObserveAsEvents
import com.moim.core.ui.view.showToast

@Composable
fun AlarmSettingRoute(
    padding: PaddingValues,
    viewModel: AlarmSettingViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
) {
    val alarmSettingUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val modifier =
        Modifier.containerScreen(
            backgroundColor = MoimTheme.colors.bg.primary,
            padding = padding,
        )
    var isPostNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED,
            )
        } else {
            mutableStateOf(true)
        }
    }
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            isPostNotificationPermission = result
        }
    val settingLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val result =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                } else {
                    true
                }
            isPostNotificationPermission = result
        }

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is AlarmSettingUiEvent.NavigateToBack -> {
                navigateToBack()
            }

            is AlarmSettingUiEvent.NavigateToSystemSetting -> {
                val intent =
                    Intent(
                        Settings.ACTION_APP_NOTIFICATION_SETTINGS,
                    ).apply { putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName) }
                settingLauncher.launch(intent)
            }

            is AlarmSettingUiEvent.ShowToastMessage -> {
                showToast(context, event.message)
            }
        }
    }

    LaunchedEffect(alarmSettingUiState) {
        if (alarmSettingUiState !is AlarmSettingUiState.Success) return@LaunchedEffect
        if (isPostNotificationPermission.not() && Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    when (val uiState = alarmSettingUiState) {
        is AlarmSettingUiState.Loading -> {
            LoadingScreen(modifier)
        }

        is AlarmSettingUiState.Success -> {
            AlarmSettingScreen(
                modifier = modifier,
                uiState = uiState,
                isPostNotificationPermission = isPostNotificationPermission,
                isLoading = isLoading,
                onUiAction = viewModel::onUiAction,
            )
        }

        is AlarmSettingUiState.Error -> {
            ErrorScreen(
                modifier = modifier,
                onClickRefresh = { viewModel.onUiAction(AlarmSettingUiAction.OnClickRefresh) },
            )
        }
    }
}

@Composable
fun AlarmSettingScreen(
    modifier: Modifier = Modifier,
    uiState: AlarmSettingUiState.Success,
    isPostNotificationPermission: Boolean,
    isLoading: Boolean,
    onUiAction: (AlarmSettingUiAction) -> Unit,
) {
    TrackScreenViewEvent(screenName = "notification_setting")
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        MoimTopAppbar(
            title = stringResource(R.string.alarm_setting_title),
            onClickNavigate = { onUiAction(AlarmSettingUiAction.OnClickBack) },
        )
        if (isPostNotificationPermission.not()) {
            AlarmSettingPermissionItem(onUiAction = onUiAction)
        }

        Spacer(Modifier.height(8.dp))

        AlarmSettingSwitchItem(
            title = stringResource(R.string.alarm_setting_meeting_notify),
            description = stringResource(R.string.alarm_setting_meeting_notify_description),
            isChecked = uiState.isSubscribeForMeetingNotify,
            onCheckedChange = { onUiAction(AlarmSettingUiAction.OnChangeMeetingNotify(it)) },
        )
        AlarmSettingSwitchItem(
            title = stringResource(R.string.alarm_setting_plan_notify),
            description = stringResource(R.string.alarm_setting_plan_notify_description),
            isChecked = uiState.isSubscribeForPlanNotify,
            onCheckedChange = { onUiAction(AlarmSettingUiAction.OnChangePlanNotify(it)) },
        )
        AlarmSettingSwitchItem(
            title = stringResource(R.string.alarm_setting_comment_notify),
            description = stringResource(R.string.alarm_setting_comment_notify_description),
            isChecked = uiState.isSubscribeForCommentNotify,
            onCheckedChange = { onUiAction(AlarmSettingUiAction.OnChangeCommentNotify(it)) },
        )
        AlarmSettingSwitchItem(
            title = stringResource(R.string.alarm_setting_mention_notify),
            description = stringResource(R.string.alarm_setting_mention_notify_description),
            isChecked = uiState.isSubscribeForMentionNotify,
            onCheckedChange = { onUiAction(AlarmSettingUiAction.OnChangeMentionNotify(it)) },
        )
    }

    LoadingDialog(isLoading)
}

@Composable
private fun AlarmSettingPermissionItem(
    modifier: Modifier = Modifier,
    onUiAction: (AlarmSettingUiAction) -> Unit,
) {
    Row(
        modifier =
            modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MoimTheme.colors.bg.input)
                .onSingleClick { onUiAction(AlarmSettingUiAction.OnClickPermissionRequest) }
                .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        MoimText(
            text = stringResource(R.string.alarm_setting_permission),
            style = MoimTheme.typography.body01.regular,
            color = MoimTheme.colors.text.text03,
        )
    }
}

@Composable
private fun AlarmSettingSwitchItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            MoimText(
                text = title,
                style = MoimTheme.typography.title03.medium,
                color = MoimTheme.colors.text.text01,
            )
            Spacer(Modifier.height(4.dp))
            MoimText(
                text = description,
                style = MoimTheme.typography.body02.regular,
                color = MoimTheme.colors.text.text03,
            )
        }

        Spacer(Modifier.width(8.dp))

        MoimSwitch(
            isChecked = isChecked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@ThemePreviews
@Composable
private fun AlarmSettingScreenPreview() {
    MoimTheme {
        val modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.bg.primary)

        AlarmSettingScreen(
            modifier = modifier,
            uiState =
                AlarmSettingUiState.Success(
                    isSubscribeForMeetingNotify = true,
                    isSubscribeForPlanNotify = false,
                    isSubscribeForCommentNotify = false,
                    isSubscribeForMentionNotify = false,
                ),
            isPostNotificationPermission = false,
            isLoading = false,
            onUiAction = {},
        )
    }
}
