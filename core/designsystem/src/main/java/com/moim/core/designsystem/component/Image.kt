package com.moim.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.moim.core.designsystem.R
import com.moim.core.designsystem.theme.color_FFFFFF

@Composable
fun NetworkImage(
    modifier: Modifier = Modifier,
    imageUrl: String? = "",
    contentDescription: String = "",
    placeholder: Painter = painterResource(R.drawable.ic_loading),
    errorImage: Painter = painterResource(R.drawable.ic_loading),
    contentScale: ContentScale = ContentScale.Crop,
) {
    if (LocalInspectionMode.current) {
        Image(
            modifier = modifier,
            painter = placeholder,
            contentDescription = null,
        )
    } else {
        AsyncImage(
            modifier = modifier,
            model = ImageRequest
                .Builder(LocalContext.current)
                .data(imageUrl)
                .build(),
            placeholder = placeholder,
            error = errorImage,
            contentDescription = contentDescription,
            contentScale = contentScale,
        )
    }
}

@Preview
@Composable
private fun NetworkImagePreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.background(color_FFFFFF)
        ) {
            NetworkImage(
                modifier = Modifier.size(120.dp),
                imageUrl = "",
                contentDescription = "",
            )
        }
    }
}