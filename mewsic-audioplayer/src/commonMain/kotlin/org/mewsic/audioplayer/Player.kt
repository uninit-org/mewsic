package org.mewsic.audioplayer

import org.mewsic.audioplayer.source.AudioSource

abstract class Player {
    var paused = true
        protected set
    open var volume = 1f

    abstract fun play(source: AudioSource)
    // abstract fun play(chunk: AudioChunk)

    open fun pause() {
        paused = true
    }
    open fun resume() {
        paused = false
    }
}

expect fun getPlayer(): Player
