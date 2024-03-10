package dev.uninit.mewsic.app.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.russhwolf.settings.PreferencesSettings
import dev.uninit.mewsic.utils.platform.DesktopPlatform
import java.util.prefs.Preferences

@Composable
actual fun PlatformCompositionLocalProvider(content: @Composable () -> Unit) {
    val platform = remember { DesktopPlatform() }
    val settings = remember { PreferencesSettings(Preferences.userRoot().node("mewsic")) }
    val context = remember { DefaultComponentContext(LifecycleRegistry()) }

    CompositionLocalProvider(
        LocalPlatform provides platform,
        LocalSettings provides settings,
        LocalComponentContext provides context,
        content = content
    )
}
