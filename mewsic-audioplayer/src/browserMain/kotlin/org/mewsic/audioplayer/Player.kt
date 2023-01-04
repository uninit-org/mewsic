package org.mewsic.audioplayer

import externals.AudioBuffer
import externals.getAudioContext
import org.mewsic.audioplayer.AudioChunk
import org.mewsic.audioplayer.Player
import org.mewsic.audioplayer.source.AudioSource

object PlayerImpl : Player() {
    private val context = getAudioContext()
    private val gain = context.createGain()
    private val source = context.createBufferSource()
    private val bufferQueue = mutableListOf<AudioBuffer>()

    init {
        gain.connect(context.destination)
        source.connect(gain)
        source.onended = {
            if (bufferQueue.isNotEmpty()) {
                source.buffer = bufferQueue.removeAt(0)
                source.start()
            } else {
                pause()
            }
        }
    }

    override fun play(source: AudioSource) {
        TODO("Not yet implemented")
    }

    override fun pause() {
        if (paused) return
        super.pause()
        source.stop()
    }

    override fun resume() {
        if (!paused) return
        super.resume()
        source.start()
    }

    fun play(chunk: AudioChunk) {
        val buffer = context.createBuffer(chunk.channels, chunk.samples, context.sampleRate)
        for (channel in 0 until chunk.channels) {
            val out = buffer.getChannelData(channel)
            val data = chunk.audioByChannel[channel]

            for (i in 0 until chunk.samples) {
                out[i] = data[i]
            }
        }
        bufferQueue.add(buffer)
        if (paused) {
            resume()
        }
    }
}

actual fun getPlayer(): Player = PlayerImpl
