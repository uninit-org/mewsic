package org.mewsic.audioplayer

import android.media.*
import kotlin.concurrent.thread

object PlayerImpl : Player() {
    private val audioStreamQueue = mutableListOf<ByteArray>()
    private val track = AudioTrack(
        AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build(),
        AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
            .setSampleRate(44100)
            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
            .build(),
        2 * 44100 * 4,
        AudioTrack.MODE_STREAM,
        AudioManager.AUDIO_SESSION_ID_GENERATE
    )

    private val playingThread = thread {
        TODO()
    }

    override fun play(chunk: AudioChunk) {
        chunk.ensureStereo()

        val bytes = ByteArray(chunk.samples * 4 * chunk.channels)
        for (channel in 0 until chunk.channels) {
            val data = chunk.audioByChannel[channel]
            for (i in 0 until chunk.samples) {
                val float = data[i]
                val int = float.toRawBits()
                bytes[i * 4 + 0] = (int shr 0).toByte()
                bytes[i * 4 + 1] = (int shr 8).toByte()
                bytes[i * 4 + 2] = (int shr 16).toByte()
                bytes[i * 4 + 3] = (int shr 24).toByte()
            }
        }

        audioStreamQueue.add(bytes)

        if (paused) {
            resume()
        }
    }
}

actual fun getPlayer(): Player = PlayerImpl
