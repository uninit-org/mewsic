package dev.uninit.mewsic.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.uninit.mewsic.api.MewsicPluginBase
import dev.uninit.mewsic.api.style.color.AppColors
import dev.uninit.mewsic.api.style.color.LocalAppColors
import dev.uninit.mewsic.api.style.theme.AppTheme
import dev.uninit.mewsic.api.style.theme.LocalAppTheme
import dev.uninit.mewsic.app.component.LocalComponentContext
import dev.uninit.mewsic.app.component.LocalSettings
import dev.uninit.mewsic.app.component.PlatformCompositionLocalProvider
import dev.uninit.mewsic.app.component.PlatformWrapper
import dev.uninit.mewsic.app.ext.bound
import dev.uninit.mewsic.app.style.color.ColorSchemeType
import dev.uninit.mewsic.app.style.color.SystemAppColors
import dev.uninit.mewsic.app.style.theme.MaterialAppTheme
import java.util.*

@Composable
fun MewsicApp() {
    val plugins = remember { ServiceLoader.load(MewsicPluginBase::class.java).toList() }
    val themes = remember { plugins.map { it.themes() }.reduce { acc, themes -> acc + themes } }

    PlatformCompositionLocalProvider {
        val context = LocalComponentContext.current
        val rootComponent = remember(context) { RootComponent(context) }
        val settings = LocalSettings.current
        val activeScheme by settings.bound<ColorSchemeType>(ColorSchemeType.System)
        val isDark = when (activeScheme) {
            ColorSchemeType.Light -> false
            ColorSchemeType.Dark -> true
            ColorSchemeType.System -> isSystemInDarkTheme()
        }
        val appColors by settings.bound<AppColors>(SystemAppColors(isDark))
        val theme by settings.bound<AppTheme.Key>(MaterialAppTheme.KEY)

        val appTheme = themes[theme] ?: themes[MaterialAppTheme.KEY]!!

        CompositionLocalProvider(
            LocalSettings provides settings,
            LocalAppTheme provides appTheme,
            LocalAppColors provides appColors
        ) {
            PlatformWrapper {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Root(rootComponent)
                }
            }
        }
    }
}
