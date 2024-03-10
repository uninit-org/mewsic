package dev.uninit.mewsic.player

import dev.uninit.mewsic.media.track.MediaTrack

class Queue {
    val items = mutableListOf<MediaTrack>()
    var loopMode = LoopMode.OFF

    fun shuffle() {
        items.shuffle()
    }

    fun clear() {
        items.clear()
    }
}
