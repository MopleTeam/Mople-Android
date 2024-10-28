package com.moim.core.designsystem.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.color_333333
import com.moim.core.designsystem.theme.color_FFFFFF

@Composable
fun MoimBottomSheetDialog(
    modifier: Modifier = Modifier,
    shapes: CornerBasedShape = RoundedCornerShape(20.dp),
    sheetState: SheetState = rememberModalBottomSheetState(true),
    onDismiss: () -> Unit,
    dragHandle: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    ModalBottomSheet(
        modifier = modifier.navigationBarsPadding(),
        sheetState = sheetState,
        shape = shapes.copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)),
        containerColor = color_FFFFFF,
        contentColor = color_333333,
        onDismissRequest = {
            keyboardController?.hide()
            focusManager.clearFocus()
            onDismiss()
        },
        dragHandle = dragHandle,
    ) {
        MoimTheme {
            content()
        }
    }
}