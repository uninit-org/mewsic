@file:Suppress("DEPRECATION")

package dev.uninit.mewsic.app.style.theme

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.uninit.mewsic.api.style.theme.LocalAppTheme

@Composable
fun AppButton(
    onClick: () -> Unit,
    color: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable (RowScope.() -> Unit)
) {
    val theme = LocalAppTheme.current

    theme.button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        color = color,
        contentColor = contentColor,
        content = content
    )
}

@Composable
fun AppButtonPrimary(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable (RowScope.() -> Unit)
) {
    val theme = LocalAppTheme.current

    theme.buttonPrimary(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        content = content
    )
}

@Composable
fun AppButtonSecondary(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable (RowScope.() -> Unit)
) {
    val theme = LocalAppTheme.current

    theme.buttonSecondary(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        content = content
    )
}


@Composable
fun AppThemeWrapper(
    content: @Composable () -> Unit
) {
    val theme = LocalAppTheme.current

    theme.wrapper(content)
}
