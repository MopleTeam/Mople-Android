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
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.imageviewer.model.ImageViewerIntent
import com.moim.feature.imageviewer.model.ImageViewerSideEffect
import com.moim.feature.imageviewer.model.ImageViewerState
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ImageViewerRoute(
    padding: PaddingValues,
    viewModel: ImageViewerViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
) {
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.global.black)
    val imageViewerUiState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ImageViewerSideEffect.NavigateToBack -> navigateToBack()
        }
    }

    ImageViewerScreen(
        modifier = modifier,
        uiState = imageViewerUiState,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun ImageViewerScreen(
    modifier: Modifier = Modifier,
    uiState: ImageViewerState,
    onIntent: (ImageViewerIntent) -> Unit,
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
        backgroundColor = MoimTheme.colors.global.black,
        topBar = {
            ImageViewerTopAppbar(
                modifier = Modifier.fillMaxWidth(),
                title = uiState.title,
                currentIndex = pagerState.currentPage + 1,
                totalIndex = pageSize,
                onClickDismiss = { onIntent(ImageViewerIntent.BackClick) },
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
        backgroundColor = MoimTheme.colors.global.black,
        navigationIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_back),
                contentDescription = "",
                tint = MoimTheme.colors.global.white,
            )
        },
        onClickNavigate = onClickDismiss,
        title = title,
        titleColor = MoimTheme.colors.global.white,
        actions = {
            if (totalIndex > 1) {
                MoimText(
                    text = "$currentIndex/$totalIndex",
                    style = MoimTheme.typography.title03.semiBold,
                    color = MoimTheme.colors.global.white,
                )
            }
        },
    )
}
