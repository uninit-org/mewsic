package dev.uninit.mewsic.app

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.singleWindowApplication
import dev.uninit.mewsic.app.component.LocalPlayer
import dev.uninit.mewsic.player.DesktopPlayer
import dev.uninit.mewsic.utils.platform.Logger


fun main() {
    val logger = Logger.withPrefix("[d.u.m.a.MainKt]")

    logger.debug("Initializing App on Desktop")

    // TODO: Use taskbar icon?

    val player = DesktopPlayer()
    player.spawn()

    singleWindowApplication(exitProcessOnExit = true) {
        CompositionLocalProvider(
            LocalPlayer provides player
        ) {
            MewsicApp()
        }
    }
}
