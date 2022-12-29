package org.mewsic.audioplayer

import org.mewsic.audioplayer.ext.toAmplitude
import org.mewsic.audioplayer.ext.toDecibels
import org.mewsic.audioplayer.AudioChunk
import org.mewsic.audioplayer.Player
import javax.sound.sampled.*

object PlayerImpl : Player() {
    private val audioStreamQueue = mutableListOf<AudioInputStream>()
    private val targetFormat = AudioFormat(44100f, 16, 2, true, false)
    private val line: SourceDataLine = AudioSystem.getSourceDataLine(targetFormat).apply {
        open(targetFormat)
        addLineListener {
            if (it.type == LineEvent.Type.STOP) {
                if (!paused && audioStreamQueue.isNotEmpty()) {
                    val audioStream = audioStreamQueue.removeAt(0)
                    val newStream = AudioSystem.getAudioInputStream(targetFormat, audioStream)
                    write(newStream.readAllBytes(), 0, newStream.available())
                } else {
                    pause()
                }
            }
        }
    }
    private val gain = line.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
    override var volume: Float
        get() = gain.value.toAmplitude()
        set(value) {
            gain.value = value.toDecibels()
        }

    override fun play(chunk: AudioChunk) {
        val currentFormat = AudioFormat(
            AudioFormat.Encoding.PCM_FLOAT,
            chunk.sampleRate.toFloat(),
            32,
            chunk.channels,
            chunk.channels * 4 * chunk.samples,
            chunk.sampleRate.toFloat(),
            false
        )

        // Combine all channels into one ByteArray
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

        audioStreamQueue.add(AudioInputStream(bytes.inputStream(), currentFormat, chunk.samples.toLong()))
        if (paused) {
            resume()
        }
    }

    override fun pause() {
        if (paused) return
        super.pause()
        line.stop()
    }

    override fun resume() {
        if (!paused) return
        super.resume()
        line.start()
    }
}

actual fun getPlayer(): Player {
    TODO("Not yet implemented")
}
