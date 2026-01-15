package com.moim.feature.imageviewer

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.view.ObserveAsEvents
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun ImageViewerRoute(
    padding: PaddingValues,
    viewModel: ImageViewerViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
) {
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.black)
    val imageViewerUiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is ImageViewerUiEvent.NavigateToBack -> navigateToBack()
        }
    }

    when (val uiState = imageViewerUiState) {
        is ImageViewerUiState -> {
            ImageViewerScreen(
                modifier = modifier,
                uiState = uiState,
                onUiAction = viewModel::onUiAction,
            )
        }
    }
}

@Composable
fun ImageViewerScreen(
    modifier: Modifier = Modifier,
    uiState: ImageViewerUiState,
    onUiAction: (ImageViewerUiAction) -> Unit,
) {
    val pageSize = uiState.images.size
    val pagerState =
        rememberPagerState(
            pageCount = { pageSize },
            initialPage = uiState.position,
        )
    val zoomState = rememberZoomState(initialScale = 1.0f)

    TrackScreenViewEvent(screenName = "image_viewer")
    MoimScaffold(
        modifier = modifier,
        backgroundColor = MoimTheme.colors.black,
        topBar = {
            ImageViewerTopAppbar(
                modifier = Modifier.fillMaxWidth(),
                title = uiState.title,
                currentIndex = pagerState.currentPage + 1,
                totalIndex = pageSize,
                onClickDismiss = { onUiAction(ImageViewerUiAction.OnClickBack) },
            )
        },
        content = {
            HorizontalPager(
                modifier = Modifier.fillMaxWidth(),
                state = pagerState,
            ) { index ->
                NetworkImage(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(vertical = 20.dp)
                            .zoomable(zoomState = zoomState),
                    imageUrl = uiState.images[index],
                    errorImage = painterResource(uiState.defaultImage ?: R.drawable.ic_empty_logo),
                    contentScale = ContentScale.Fit,
                )
            }
        },
    )
}

@Composable
private fun ImageViewerTopAppbar(
    modifier: Modifier = Modifier,
    title: String,
    currentIndex: Int,
    totalIndex: Int,
    onClickDismiss: () -> Unit,
) {
    MoimTopAppbar(
        modifier = modifier,
        backgroundColor = MoimTheme.colors.black,
        navigationIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_back),
                contentDescription = "",
                tint = MoimTheme.colors.white,
            )
        },
        onClickNavigate = onClickDismiss,
        title = title,
        titleColor = MoimTheme.colors.white,
        actions = {
            if (totalIndex > 1) {
                MoimText(
                    text = "$currentIndex/$totalIndex",
                    style = MoimTheme.typography.title03.semiBold,
                    color = MoimTheme.colors.gray.gray04,
                )
            }
        },
    )
}
