@file:Suppress("DEPRECATION")

package dev.uninit.mewsic.api.style.theme

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.uninit.mewsic.api.style.color.AppColors
import dev.uninit.mewsic.api.style.color.LocalAppColors
import kotlinx.serialization.Serializable

interface AppTheme {
    @Serializable
    data class Key(val name: String)

    val colors: AppColors
        @Composable
        get() = LocalAppColors.current


    @Composable
    @Deprecated(message = "This method should not be used directly", replaceWith = ReplaceWith("AppThemeWrapper"), level = DeprecationLevel.WARNING)
    fun wrapper(content: @Composable () -> Unit)

    @Composable
    @Deprecated(message = "This method should not be used directly", replaceWith = ReplaceWith("AppButton"), level = DeprecationLevel.WARNING)
    fun button(
        onClick: () -> Unit,
        color: Color,
        contentColor: Color,
        modifier: Modifier,
        enabled: Boolean,
        content: @Composable (RowScope.() -> Unit)
    )

    @Composable
    @Deprecated(message = "This method should not be used directly", replaceWith = ReplaceWith("AppButtonPrimary"), level = DeprecationLevel.WARNING)
    fun buttonPrimary(
        onClick: () -> Unit,
        modifier: Modifier,
        enabled: Boolean,
        content: @Composable (RowScope.() -> Unit)
    ) = button(onClick, colors.primary(), colors.onPrimary(), modifier, enabled, content)

    @Composable
    @Deprecated(message = "This method should not be used directly", replaceWith = ReplaceWith("AppButtonSecondary"), level = DeprecationLevel.WARNING)
    fun buttonSecondary(
        onClick: () -> Unit,
        modifier: Modifier,
        enabled: Boolean,
        content: @Composable (RowScope.() -> Unit)
    ) = button(onClick, colors.secondary(), colors.onSecondary(), modifier, enabled, content)
}

val LocalAppTheme = staticCompositionLocalOf<AppTheme> { error("No theme provided") }
