package dev.uninit.mewsic.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.russhwolf.settings.get
import dev.uninit.mewsic.app.component.LocalComponentContext
import dev.uninit.mewsic.app.component.LocalSettings
import dev.uninit.mewsic.app.component.MewsicTheme
import dev.uninit.mewsic.app.component.PlatformCompositionLocalProvider

@Composable
fun MewsicApp() {
    PlatformCompositionLocalProvider {
        val context = LocalComponentContext.current
        val rootComponent = remember(context) { RootComponent(context) }
        val settings = LocalSettings.current

        MewsicTheme(
            isDarkTheme = settings["dark_theme", isSystemInDarkTheme()]
        ) {
            Root(rootComponent)
        }
    }
}
