package com.moim.feature.reviewwrite

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.common.model.ViewIdType
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.component.MoimPrimaryButton
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.view.ObserveAsEvents
import com.moim.core.ui.view.showToast
import com.moim.feature.reviewwrite.ui.ReviewWritePlanInfo
import com.moim.feature.reviewwrite.ui.ReviewWriteUploadImageContainer

@Composable
fun ReviewWriteRoute(
    viewModel: ReviewWriteViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    navigateToParticipants: (ViewIdType) -> Unit,
) {
    val context = LocalContext.current
    val reviewWriteUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.white, padding = padding)
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val multiPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(5),
        onResult = { uris -> if (uris.isNotEmpty()) viewModel.onUiAction(ReviewWriteUiAction.OnClickAddImages(uris.map { it.toString() })) }
    )

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is ReviewWriteUiEvent.NavigateToBack -> navigateToBack()
            is ReviewWriteUiEvent.NavigateToParticipants -> navigateToParticipants(event.viewIdType)
            is ReviewWriteUiEvent.NavigateToPhotoPicker -> multiPhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            is ReviewWriteUiEvent.ShowToastMessage -> showToast(context, event.toastMessage)
        }
    }


    when (val uiState = reviewWriteUiState) {
        is ReviewWriteUiState.Loading -> LoadingScreen(modifier)

        is ReviewWriteUiState.Success -> ReviewWriteScreen(
            modifier = modifier,
            uiState = uiState,
            isLoading = isLoading,
            onUiAction = viewModel::onUiAction
        )

        is ReviewWriteUiState.Error -> ErrorScreen(modifier = modifier) {
            viewModel.onUiAction(ReviewWriteUiAction.OnClickRefresh)
        }
    }
}

@Composable
fun ReviewWriteScreen(
    modifier: Modifier = Modifier,
    uiState: ReviewWriteUiState.Success,
    isLoading: Boolean,
    onUiAction: (ReviewWriteUiAction) -> Unit,
) {
    TrackScreenViewEvent(screenName = "review_write")
    MoimScaffold(
        modifier = modifier,
        topBar = {
            MoimTopAppbar(
                title = stringResource(if (uiState.isUpdated) R.string.review_write_title_update else R.string.review_write_title_create),
                onClickNavigate = { onUiAction(ReviewWriteUiAction.OnClickBack) }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(it)
            ) {
                if (!uiState.isUpdated) {
                    ReviewWriteHeader()
                    ReviewWriteDivider()
                }

                ReviewWriteUploadImageContainer(
                    images = uiState.uploadImages,
                    onUiAction = onUiAction
                )
                ReviewWriteDivider()
                ReviewWritePlanInfo(
                    review = uiState.review,
                    onUiAction = onUiAction
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                MoimPrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    enable = uiState.enableSubmit,
                    text = if (uiState.isUpdated) stringResource(R.string.review_write_update) else stringResource(R.string.review_write_create),
                    onClick = { onUiAction(ReviewWriteUiAction.OnClickSubmit) }
                )
            }
        }
    )

    LoadingDialog(isLoading)
}

@Composable
private fun ReviewWriteHeader() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        MoimText(
            text = stringResource(R.string.review_write_header),
            style = MoimTheme.typography.heading.bold,
            color = MoimTheme.colors.gray.gray01,
            singleLine = false
        )
    }
}

@Composable
private fun ReviewWriteDivider() {
    HorizontalDivider(
        color = MoimTheme.colors.bg.input,
        thickness = 8.dp
    )
}