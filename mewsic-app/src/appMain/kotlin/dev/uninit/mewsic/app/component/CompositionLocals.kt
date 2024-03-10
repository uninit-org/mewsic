package dev.uninit.mewsic.app.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.arkivanov.decompose.ComponentContext
import com.russhwolf.settings.Settings
import dev.uninit.mewsic.player.Player
import dev.uninit.mewsic.utils.platform.Platform

val LocalPlatform = compositionLocalOf<Platform> { error("No platform specified") }
val LocalSettings = compositionLocalOf<Settings> { error("No settings specified") }
val LocalComponentContext = compositionLocalOf<ComponentContext> { error("No component context specified") }
val LocalPlayer = compositionLocalOf<Player> { error("No player specified") }

@Composable
expect fun PlatformCompositionLocalProvider(
    content: @Composable () -> Unit
)
