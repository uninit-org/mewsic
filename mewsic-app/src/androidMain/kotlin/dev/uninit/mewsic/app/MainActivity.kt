package dev.uninit.mewsic.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.arkivanov.decompose.defaultComponentContext
import dev.uninit.mewsic.app.component.LocalComponentContext
import dev.uninit.mewsic.app.component.LocalPlayer
import dev.uninit.mewsic.player.AndroidPlayer
import dev.uninit.mewsic.utils.platform.logger

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        logger.debug("Initializing App on Android")

        val context = defaultComponentContext()
        val player = AndroidPlayer(this)
        player.spawn()

        setContent {
            CompositionLocalProvider(
                LocalComponentContext provides context,
                LocalPlayer provides player,
            ) {
                MewsicApp()
            }
        }
    }
}
