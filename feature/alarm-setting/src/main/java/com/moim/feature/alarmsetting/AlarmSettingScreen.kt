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
import com.moim.core.common.result.data
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
import com.moim.core.ui.view.showToast
import com.moim.feature.alarmsetting.model.AlarmSettingIntent
import com.moim.feature.alarmsetting.model.AlarmSettingSideEffect
import com.moim.feature.alarmsetting.model.NotifySetting
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun AlarmSettingRoute(
    padding: PaddingValues,
    viewModel: AlarmSettingViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
) {
    val alarmSettingUiState by viewModel.collectAsState()
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

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is AlarmSettingSideEffect.NavigateToBack -> {
                navigateToBack()
            }

            is AlarmSettingSideEffect.NavigateToSystemSetting -> {
                val intent =
                    Intent(
                        Settings.ACTION_APP_NOTIFICATION_SETTINGS,
                    ).apply { putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName) }
                settingLauncher.launch(intent)
            }

            is AlarmSettingSideEffect.ShowToastMessage -> {
                showToast(context, sideEffect.message)
            }
        }
    }

    LaunchedEffect(alarmSettingUiState) {
        if (!alarmSettingUiState.isSuccess) return@LaunchedEffect
        if (isPostNotificationPermission.not() && Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    when {
        alarmSettingUiState.isLoading -> {
            LoadingScreen(modifier)
        }

        alarmSettingUiState.isSuccess -> {
            AlarmSettingScreen(
                modifier = modifier,
                notifySetting = alarmSettingUiState.notifySetting.data ?: NotifySetting(),
                isPostNotificationPermission = isPostNotificationPermission,
                isLoading = isLoading,
                onIntent = viewModel::onIntent,
            )
        }

        alarmSettingUiState.isError -> {
            ErrorScreen(
                modifier = modifier,
                onClickRefresh = { viewModel.onIntent(AlarmSettingIntent.RefreshClick) },
            )
        }
    }
}

@Composable
fun AlarmSettingScreen(
    modifier: Modifier = Modifier,
    notifySetting: NotifySetting,
    isPostNotificationPermission: Boolean,
    isLoading: Boolean,
    onIntent: (AlarmSettingIntent) -> Unit,
) {
    TrackScreenViewEvent(screenName = "notification_setting")
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        MoimTopAppbar(
            title = stringResource(R.string.alarm_setting_title),
            onClickNavigate = { onIntent(AlarmSettingIntent.BackClick) },
        )
        if (isPostNotificationPermission.not()) {
            AlarmSettingPermissionItem(onIntent = onIntent)
        }

        Spacer(Modifier.height(8.dp))

        AlarmSettingSwitchItem(
            title = stringResource(R.string.alarm_setting_meeting_notify),
            description = stringResource(R.string.alarm_setting_meeting_notify_description),
            isChecked = notifySetting.isSubscribeForMeetingNotify,
            onCheckedChange = { onIntent(AlarmSettingIntent.MeetingNotifyChange(it)) },
        )
        AlarmSettingSwitchItem(
            title = stringResource(R.string.alarm_setting_plan_notify),
            description = stringResource(R.string.alarm_setting_plan_notify_description),
            isChecked = notifySetting.isSubscribeForPlanNotify,
            onCheckedChange = { onIntent(AlarmSettingIntent.PlanNotifyChange(it)) },
        )
        AlarmSettingSwitchItem(
            title = stringResource(R.string.alarm_setting_comment_notify),
            description = stringResource(R.string.alarm_setting_comment_notify_description),
            isChecked = notifySetting.isSubscribeForCommentNotify,
            onCheckedChange = { onIntent(AlarmSettingIntent.CommentNotifyChange(it)) },
        )
        AlarmSettingSwitchItem(
            title = stringResource(R.string.alarm_setting_mention_notify),
            description = stringResource(R.string.alarm_setting_mention_notify_description),
            isChecked = notifySetting.isSubscribeForMentionNotify,
            onCheckedChange = { onIntent(AlarmSettingIntent.MentionNotifyChange(it)) },
        )
    }

    LoadingDialog(isLoading)
}

@Composable
private fun AlarmSettingPermissionItem(
    modifier: Modifier = Modifier,
    onIntent: (AlarmSettingIntent) -> Unit,
) {
    Row(
        modifier =
            modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MoimTheme.colors.bg.input)
                .onSingleClick { onIntent(AlarmSettingIntent.PermissionRequestClick) }
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
            notifySetting =
                NotifySetting(
                    isSubscribeForMeetingNotify = true,
                    isSubscribeForPlanNotify = false,
                    isSubscribeForCommentNotify = false,
                    isSubscribeForMentionNotify = false,
                ),
            isPostNotificationPermission = false,
            isLoading = false,
            onIntent = {},
        )
    }
}
