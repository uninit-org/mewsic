package dev.uninit.mewsic.app.style.theme

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.uninit.mewsic.api.style.theme.AppTheme
import dev.uninit.mewsic.app.ext.withSaturationDelta
import dev.uninit.mewsic.app.ext.withValueDelta
import dev.uninit.mewsic.app.generated.Fredoka_VariableFont
import dev.uninit.mewsic.app.generated.Res
import org.jetbrains.compose.resources.Font

object CuteAppTheme : AppTheme {
    val KEY = AppTheme.Key("Cute")

    @Deprecated(
        "This method should not be used directly",
        replaceWith = ReplaceWith("AppThemeWrapper"),
        level = DeprecationLevel.WARNING
    )
    @Composable
    override fun wrapper(content: @Composable () -> Unit) {
        val fontFamily = FontFamily(Font(Res.font.Fredoka_VariableFont))

        MaterialTheme(
            typography = Typography(
                displayLarge = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = fontFamily
                ),
                displayMedium = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = fontFamily
                ),
                displaySmall = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = fontFamily
                ),
                headlineLarge = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = fontFamily
                ),
                headlineMedium = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = fontFamily
                ),
                headlineSmall = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = fontFamily
                ),
                titleLarge = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = fontFamily
                ),
                titleMedium = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = fontFamily
                ),
                titleSmall = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = fontFamily
                ),
                bodyLarge = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = fontFamily
                ),
                bodyMedium = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = fontFamily
                ),
                bodySmall = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = fontFamily
                ),
                labelLarge = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = fontFamily
                ),
                labelMedium = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = fontFamily
                ),
                labelSmall = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = fontFamily
                ),
            ),
            content = content
        )
    }

    private enum class ButtonState {
        Full,
        Pressed,
        Hovered,
        Disabled,
    }

    @Deprecated(
        "This method should not be used directly",
        replaceWith = ReplaceWith("AppButton"),
        level = DeprecationLevel.WARNING
    )
    @Composable
    override fun button(
        onClick: () -> Unit,
        color: Color,
        contentColor: Color,
        modifier: Modifier,
        enabled: Boolean,
        content: @Composable (RowScope.() -> Unit)
    ) {
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
                ButtonState.Pressed -> color.withValueDelta(0.15f)
                ButtonState.Hovered -> color.withValueDelta(-0.05f)
                ButtonState.Disabled -> color.withSaturationDelta(-0.2f).withValueDelta(-0.15f)
            },
            animationSpec = spring(stiffness = animationStiffness)
        )
        val borderColor by animateColorAsState(
            when (state) {
                ButtonState.Full, ButtonState.Hovered -> color.withValueDelta(-0.2f)
                ButtonState.Pressed -> color.withSaturationDelta(0.1f)
                ButtonState.Disabled -> color.withSaturationDelta(-0.15f).withValueDelta(-0.3f)
            },
            animationSpec = spring(stiffness = animationStiffness)
        )
        val shadowColor by animateColorAsState(
            when (state) {
                ButtonState.Full, ButtonState.Hovered, ButtonState.Pressed -> color.withValueDelta(-0.3f)
                ButtonState.Disabled -> color.withSaturationDelta(-0.2f).withValueDelta(-0.35f)
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
}
