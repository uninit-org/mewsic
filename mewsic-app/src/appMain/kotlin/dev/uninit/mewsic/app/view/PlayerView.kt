package dev.uninit.mewsic.app.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import dev.uninit.mewsic.app.component.LocalPlayer
import dev.uninit.mewsic.client.soundcloud.PublicSoundCloudClient
import dev.uninit.mewsic.media.provider.SoundCloudMediaProvider
import dev.uninit.mewsic.utils.platform.Logger
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlayerComponent(ctx: ComponentContext) : ComponentContext by ctx {

}

@Composable
fun PlayerView(component: PlayerComponent) {
    val logger = remember { Logger.withPrefix("[d.u.m.a.v.PlayerView]") }
    val scope = rememberCoroutineScope()

    val client = remember { PublicSoundCloudClient() }
    val provider = remember(client) { SoundCloudMediaProvider(client) }
    val player = LocalPlayer.current

    LaunchedEffect(client) {
        client.initialSetup()
    }

    Column {
        Button(
            onClick = {
                scope.launch {
                    // TODO: remove this line, it's just for testing
                    player.queue.items.add(provider.searchTracks("waterflame electroman adventures v2").first())
                    player.play()
                }
            }
        ) {
            Text("Play")
        }

        Button(
            onClick = {
                scope.launch {
                    player.pause()
                }
            }
        ) {
            Text("Pause")
        }
    }
}
