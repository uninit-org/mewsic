package dev.uninit.mewsic.app.component

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import dev.uninit.mewsic.api.style.color.LocalAppColors
import dev.uninit.mewsic.app.ext.bound
import dev.uninit.mewsic.app.style.color.ColorSchemeType

@Composable
actual fun PlatformWrapper(content: @Composable () -> Unit)  {
    val settings = LocalSettings.current
    val activeScheme by settings.bound<ColorSchemeType>(ColorSchemeType.System)
    val systemIsDark = isSystemInDarkTheme()
    val isDark by remember {
        derivedStateOf {
            when (activeScheme) {
                ColorSchemeType.Light -> false
                ColorSchemeType.Dark -> true
                ColorSchemeType.System -> systemIsDark
            }
        }
    }


    val view = LocalView.current
    if (!view.isInEditMode) {
        val colors = LocalAppColors.current
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.background().toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    content()
}

