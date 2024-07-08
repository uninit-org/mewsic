package dev.uninit.mewsic.app.style.theme

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.uninit.mewsic.api.style.theme.AppTheme
import dev.uninit.mewsic.app.ext.withSaturationFactor

object MaterialAppTheme : AppTheme {
    val KEY = AppTheme.Key("Material")

    @Deprecated(
        "This method should not be used directly",
        replaceWith = ReplaceWith("AppThemeWrapper"),
        level = DeprecationLevel.WARNING
    )
    @Composable
    override fun wrapper(content: @Composable () -> Unit) {
        MaterialTheme(content=content)
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
        val colors = remember {
            ButtonColors(
                color,
                contentColor,
                color.withSaturationFactor(0.6f),
                contentColor.withSaturationFactor(0.6f),
            )
        }

        Button(onClick = onClick, modifier = modifier, enabled = enabled, content = content, colors=colors)
    }
}
