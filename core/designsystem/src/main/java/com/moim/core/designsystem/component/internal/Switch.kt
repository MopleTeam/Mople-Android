package com.moim.core.designsystem.component.internal

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.SnapSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateMeasurement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

internal val SwitchWidth = 48.dp
internal val SwitchHeight = 28.dp
internal val ThumbDiameter = 24.dp
internal val ThumbPadding = (SwitchHeight - ThumbDiameter) / 2
internal val TrackOutlineWidth = 2.dp
internal val SnapSpec = SnapSpec<Float>()
internal val AnimationSpec = TweenSpec<Float>(durationMillis = 100)

@Composable
internal fun MoimSwitchImpl(
    modifier: Modifier = Modifier,
    checked: Boolean,
    colors: SwitchColors,
    onCheckedChange: (Boolean) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val trackShape = RoundedCornerShape(50)
    val trackColor = if (checked) colors.checkedTrackColor else colors.uncheckedTrackColor
    val resolvedThumbColor = if (checked) colors.checkedThumbColor else colors.uncheckedThumbColor
    val borderColor = if (checked) colors.checkedBorderColor else colors.uncheckedBorderColor

    Box(
        modifier =
            modifier
                .minimumInteractiveComponentSize()
                .toggleable(
                    value = checked,
                    onValueChange = onCheckedChange,
                    enabled = true,
                    role = Role.Switch,
                    interactionSource = interactionSource,
                    indication = null,
                ).border(TrackOutlineWidth, borderColor, trackShape)
                .requiredSize(SwitchWidth, SwitchHeight)
                .background(trackColor, trackShape),
    ) {
        Box(
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .then(ThumbElement(interactionSource, checked))
                    .indication(
                        interactionSource = interactionSource,
                        indication = rippleOrFallbackImplementation(bounded = false, radius = 40.dp),
                    ).background(resolvedThumbColor, CircleShape),
        )
    }
}

@Suppress("DEPRECATION_ERROR")
@Composable
internal fun rippleOrFallbackImplementation(
    bounded: Boolean = true,
    radius: Dp = Dp.Unspecified,
    color: Color = Color.Unspecified,
): Indication = rememberRipple(bounded, radius, color)

internal data class ThumbElement(
    val interactionSource: InteractionSource,
    val checked: Boolean,
) : ModifierNodeElement<ThumbNode>() {
    override fun create() = ThumbNode(interactionSource, checked)

    override fun update(node: ThumbNode) {
        node.interactionSource = interactionSource
        if (node.checked != checked) {
            node.invalidateMeasurement()
        }
        node.checked = checked
        node.update()
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "switchThumb"
        properties["interactionSource"] = interactionSource
        properties["checked"] = checked
    }
}

internal class ThumbNode(
    var interactionSource: InteractionSource,
    var checked: Boolean,
) : Modifier.Node(),
    LayoutModifierNode {
    override val shouldAutoInvalidate: Boolean
        get() = false

    private var isPressed = false
    private var offsetAnim: Animatable<Float, AnimationVector1D>? = null
    private var sizeAnim: Animatable<Float, AnimationVector1D>? = null
    private var initialOffset: Float = Float.NaN
    private var initialSize: Float = Float.NaN

    override fun onAttach() {
        coroutineScope.launch {
            var pressCount = 0
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> pressCount++
                    is PressInteraction.Release -> pressCount--
                    is PressInteraction.Cancel -> pressCount--
                }
                val pressed = pressCount > 0
                if (isPressed != pressed) {
                    isPressed = pressed
                    invalidateMeasurement()
                }
            }
        }
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val size = ThumbDiameter.toPx()
        val actualSize = (sizeAnim?.value ?: size).toInt()
        val placeable = measurable.measure(Constraints.fixed(actualSize, actualSize))
        val thumbPaddingStart = (SwitchHeight - size.toDp()) / 2f
        val minBound = thumbPaddingStart.toPx()
        val thumbPathLength = (SwitchWidth - ThumbDiameter) - ThumbPadding
        val maxBound = thumbPathLength.toPx()
        val offset =
            when {
                isPressed && checked -> maxBound - TrackOutlineWidth.toPx()
                isPressed && !checked -> TrackOutlineWidth.toPx()
                checked -> maxBound
                else -> minBound
            }

        if (sizeAnim?.targetValue != size) {
            coroutineScope.launch {
                sizeAnim?.animateTo(size, if (isPressed) SnapSpec else AnimationSpec)
            }
        }

        if (offsetAnim?.targetValue != offset) {
            coroutineScope.launch {
                offsetAnim?.animateTo(offset, if (isPressed) SnapSpec else AnimationSpec)
            }
        }

        if (initialSize.isNaN() && initialOffset.isNaN()) {
            initialSize = size
            initialOffset = offset
        }

        return layout(actualSize, actualSize) {
            placeable.placeRelative(offsetAnim?.value?.toInt() ?: offset.toInt(), 0)
        }
    }

    fun update() {
        if (sizeAnim == null && !initialSize.isNaN()) {
            sizeAnim = Animatable(initialSize)
        }

        if (offsetAnim == null && !initialOffset.isNaN()) offsetAnim = Animatable(initialOffset)
    }
}
