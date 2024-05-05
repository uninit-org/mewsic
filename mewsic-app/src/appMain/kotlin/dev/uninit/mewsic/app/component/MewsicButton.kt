package dev.uninit.mewsic.app.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private fun Color.rgbToHsv(): Triple<Float, Float, Float> {
    // H in [0 .. 360]
    // S in [0 .. 1]
    // V in [0 .. 1]
    val argb = toArgb()
    val r = argb shr 16 and 0xFF
    val g = argb shr 8 and 0xFF
    val b = argb and 0xFF
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = (max - min).toFloat()

    var h = when {
        delta == 0f -> 0f
        max == r -> 60f * ((g - b) / delta % 6)
        max == g -> 60f * ((b - r) / delta + 2)
        else -> 60f * ((r - g) / delta + 4)
    }

    if (h < 0) h += 360f

    val s = if (max == 0) 0f else delta / max
    val v = max / 255f

    return Triple(h, s, v)
}

private fun colorWithValue(color: Color, value: Float): Color {
    // Convert from RGB to HSV
    val hsv = color.rgbToHsv()

    return Color.hsv(hsv.first, hsv.second, (hsv.third + value).coerceIn(0f, 1f))
}

private fun colorWithSatValue(color: Color, sat: Float, value: Float): Color {
    // Convert from RGB to HSV
    val hsv = color.rgbToHsv()

    return Color.hsv(hsv.first, (hsv.second + sat).coerceIn(0f, 1f), (hsv.third + value).coerceIn(0f, 1f))
}

private enum class ButtonState {
    Full,
    Pressed,
    Hovered,
    Disabled,
}

@Composable
fun MewsicButton(
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = contentColorFor(color),
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable (RowScope.() -> Unit)
) {
    // Behavior:
    // - 3d-style button
    // - slightly presses and darkens on hover
    // - fully presses and lightens on click
    val source = remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()
    val isHover by source.collectIsHoveredAsState()


    val state by derivedStateOf {
        when {
            !enabled -> ButtonState.Disabled
            isPressed -> ButtonState.Pressed
            isHover -> ButtonState.Hovered
            else -> ButtonState.Full
        }
    }

    // How far up the button moves
    val animationStiffness = Spring.StiffnessMedium
    val heightOffset by animateDpAsState(
        when (state) {
            ButtonState.Full -> 8.dp
            ButtonState.Disabled, ButtonState.Hovered -> 4.dp
            ButtonState.Pressed -> 0.5.dp
        },
        animationSpec = spring(stiffness = animationStiffness, visibilityThreshold = Dp.VisibilityThreshold)
    )
    val buttonColor by animateColorAsState(
        when (state) {
            ButtonState.Full -> color
            ButtonState.Pressed -> colorWithValue(color, 0.15f)
            ButtonState.Hovered -> colorWithValue(color, -0.05f)
            ButtonState.Disabled -> colorWithSatValue(color, -0.2f, -0.15f)
        },
        animationSpec = spring(stiffness = animationStiffness)
    )
    val borderColor by animateColorAsState(
        when (state) {
            ButtonState.Full, ButtonState.Hovered -> colorWithValue(color, -0.2f)
            ButtonState.Pressed -> colorWithSatValue(color, 0.1f, 0f)
            ButtonState.Disabled -> colorWithSatValue(color, -0.15f, -0.3f)
        },
        animationSpec = spring(stiffness = animationStiffness)
    )
    val shadowColor by animateColorAsState(
        when (state) {
            ButtonState.Full, ButtonState.Hovered, ButtonState.Pressed -> colorWithValue(color, -0.3f)
            ButtonState.Disabled -> colorWithSatValue(color, -0.2f, -0.35f)
        },
        animationSpec = spring(stiffness = animationStiffness)
    )

    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .padding(top = 8.dp)
            .hoverable(interactionSource = source)
            .clickable(
                interactionSource = source,
                indication = null,
                enabled = enabled,
            ) {
                onClick()
            }
            .background(
                color = shadowColor,
                shape = shape
            ),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalTextStyle provides MaterialTheme.typography.labelLarge
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    // FIXME: Downward fill for simulating button height; currently just fills the rect instead
                    .offset(y = -heightOffset)  // Button content height
                    .border(4.dp, borderColor, shape) // Border

                    .background(
                        color = buttonColor,
                        shape = shape
                    )
                    .padding(ButtonDefaults.ContentPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }
    }
}
