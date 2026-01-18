package com.moim.feature.themesetting

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.common.model.Theme
import com.moim.core.designsystem.R
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.color_171717
import com.moim.core.designsystem.theme.color_FFFFFF
import com.moim.core.ui.view.ObserveAsEvents
import kotlinx.coroutines.launch

@Composable
fun ThemeSettingRoute(
    viewModel: ThemeSettingViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToBack: () -> Unit,
) {
    val modifier =
        Modifier.containerScreen(
            backgroundColor = MoimTheme.colors.bg.primary,
            padding = padding,
        )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val graphicsLayer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val bitmapVisibility = remember { Animatable(1f) }

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is ThemeSettingUiEvent.NavigateToBack -> navigateToBack()
        }
    }

    (uiState as? ThemeSettingUiState)?.let {
        Box(modifier.fillMaxSize()) {
            ThemeSettingScreen(
                modifier =
                    Modifier.drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    },
                uiState = it,
                onUiAction = { uiAction ->
                    if (uiAction is ThemeSettingUiAction.OnClickTheme) {
                        scope.launch {
                            bitmap = graphicsLayer.toImageBitmap()
                            bitmapVisibility.snapTo(1f)
                            bitmapVisibility.animateTo(0f, tween(500, easing = EaseOutQuad))
                            bitmap = null
                        }
                    }
                    viewModel.onUiAction(uiAction)
                },
            )

            bitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .fillMaxHeight(fraction = bitmapVisibility.value)
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.BottomCenter,
                )
            }
        }
    }
}

@Composable
private fun ThemeSettingScreen(
    modifier: Modifier = Modifier,
    uiState: ThemeSettingUiState,
    onUiAction: (ThemeSettingUiAction) -> Unit,
) {
    MoimScaffold(
        modifier = modifier,
        topBar = {
            MoimTopAppbar(
                title = stringResource(R.string.theme_setting),
                onClickNavigate = { onUiAction(ThemeSettingUiAction.OnClickBack) },
            )
        },
        content = {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(it)
                        .padding(12.dp),
            ) {
                Text(
                    text = stringResource(R.string.theme_setting_title),
                    style = MoimTheme.typography.heading.bold,
                    color = MoimTheme.colors.text.text01,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.theme_setting_description),
                    style = MoimTheme.typography.title03.medium,
                    color = MoimTheme.colors.text.text04,
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ThemeItem(
                        modifier = Modifier.weight(1f),
                        isSelected = uiState.theme == Theme.SYSTEM,
                        theme = Theme.SYSTEM,
                        onSelectTheme = { theme -> onUiAction(ThemeSettingUiAction.OnClickTheme(theme)) },
                    )
                    ThemeItem(
                        modifier = Modifier.weight(1f),
                        isSelected = uiState.theme == Theme.DARK,
                        theme = Theme.DARK,
                        onSelectTheme = { theme -> onUiAction(ThemeSettingUiAction.OnClickTheme(theme)) },
                    )
                    ThemeItem(
                        modifier = Modifier.weight(1f),
                        isSelected = uiState.theme == Theme.LIGHT,
                        theme = Theme.LIGHT,
                        onSelectTheme = { theme -> onUiAction(ThemeSettingUiAction.OnClickTheme(theme)) },
                    )
                }
            }
        },
    )
}

@Composable
private fun ThemeItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    theme: Theme,
    onSelectTheme: (Theme) -> Unit,
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable(onClick = { onSelectTheme(theme) }),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (theme) {
            Theme.SYSTEM -> {
                Box {
                    ThemeItemBg(
                        modifier =
                            Modifier.drawWithContent {
                                clipRect(right = size.width / 2) {
                                    this@drawWithContent.drawContent()
                                }
                            },
                        isSelected = isSelected,
                        backgroundColor = color_FFFFFF,
                    ) {
                        Image(
                            modifier = Modifier.size(46.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.ic_logo_full_light),
                            contentDescription = "",
                        )
                    }
                    ThemeItemBg(
                        modifier =
                            Modifier.drawWithContent {
                                clipRect(left = size.width / 2) {
                                    this@drawWithContent.drawContent()
                                }
                            },
                        isSelected = isSelected,
                        backgroundColor = color_171717,
                    ) {
                        Image(
                            modifier = Modifier.size(46.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.ic_logo_full_dark),
                            contentDescription = "",
                        )
                    }
                }
            }

            Theme.DARK -> {
                ThemeItemBg(
                    isSelected = isSelected,
                    backgroundColor = color_171717,
                ) {
                    Image(
                        modifier = Modifier.size(46.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_logo_full_dark),
                        contentDescription = "",
                    )
                }
            }

            Theme.LIGHT -> {
                ThemeItemBg(
                    isSelected = isSelected,
                    backgroundColor = color_FFFFFF,
                ) {
                    Image(
                        modifier = Modifier.size(46.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_logo_full_light),
                        contentDescription = "",
                    )
                }
            }
        }

        Text(
            modifier = Modifier.padding(top = 6.dp),
            text = theme.name,
            style =
                if (isSelected) {
                    MoimTheme.typography.title03.bold
                } else {
                    MoimTheme.typography.title03.medium
                },
            color =
                if (isSelected) {
                    MoimTheme.colors.global.primary
                } else {
                    MoimTheme.colors.text.text01
                },
        )
    }
}

@Composable
private fun ThemeItemBg(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    isSelected: Boolean,
    content: @Composable BoxScope.() -> Unit,
) {
    val borderColor =
        if (isSelected) {
            MoimTheme.colors.global.primary
        } else {
            MoimTheme.colors.gray.gray05
        }

    Box(
        modifier =
            modifier
                .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(14.dp))
                .background(backgroundColor)
                .padding(12.dp),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

@ThemePreviews
@Composable
private fun ThemeItemPreview() {
    MoimTheme {
        ThemeSettingScreen(
            uiState =
                ThemeSettingUiState(
                    theme = Theme.LIGHT,
                ),
            onUiAction = {},
        )
    }
}
