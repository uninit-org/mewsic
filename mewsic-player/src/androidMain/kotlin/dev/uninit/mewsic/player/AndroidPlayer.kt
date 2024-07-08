package dev.uninit.mewsic.player

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import dev.uninit.mewsic.utils.platform.makeLogger
import kotlinx.coroutines.delay
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AndroidPlayer(private val ctx: Context) : Player() {
    private val logger = makeLogger()
    private val bufferSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_FLOAT)
    private val track = AudioTrack(
        AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build(),
        AudioFormat.Builder()
            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
            .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
            .setSampleRate(44100)
            .build(),
        bufferSize,
        AudioTrack.MODE_STREAM,
        AudioManager.AUDIO_SESSION_ID_GENERATE,
    )

    override suspend fun loop() {
        track.play()
        val buffer = ByteArray(bufferSize)
        val floatBuf = FloatArray(bufferSize / 4)

        while (true) {
            while (state != PlayerState.PLAYING) {
                delay(10L)
            }

            while (currentTrack == null || currentStream == null) {
                delay(100L)
            }

            val read = currentStream!!.copyInto(buffer, 0, buffer.size)
            val buf = ByteBuffer.wrap(buffer).also { it.order(ByteOrder.BIG_ENDIAN) }
            for (i in 0 until read / 4) {
                floatBuf[i] = buf.float
            }

            if (read == -1) {
                nextTrack()
            } else {
                val err = track.write(floatBuf, 0, read / 4, AudioTrack.WRITE_BLOCKING)
                if (err < 0) {
                    logger.error("Error writing to audio track: $err")
                }
                track.flush()
            }
        }
    }
}
