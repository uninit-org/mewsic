package dev.uninit.mewsic.app.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.russhwolf.settings.SharedPreferencesSettings
import dev.uninit.mewsic.utils.platform.AndroidPlatform

@Composable
actual fun PlatformCompositionLocalProvider(content: @Composable () -> Unit) {
    val context = LocalContext.current

    val platform = remember(context) { AndroidPlatform(context) }
    val settings = remember(context) { SharedPreferencesSettings(context.getSharedPreferences("mewsic", 0)) }

    CompositionLocalProvider(
        LocalPlatform provides platform,
        LocalSettings provides settings,
        content = content
    )
}
