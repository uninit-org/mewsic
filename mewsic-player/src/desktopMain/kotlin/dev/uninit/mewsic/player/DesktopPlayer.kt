package dev.uninit.mewsic.player

import dev.uninit.mewsic.utils.platform.logger
import kotlinx.coroutines.delay
import java.util.*
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem

class DesktopPlayer : Player() {
    private val isLinux = System.getProperty("os.name").lowercase(Locale.getDefault()).contains("linux")
    private val frameSize = 1024

    private val format = if (false) {
        AudioFormat(AudioFormat.Encoding.PCM_FLOAT, 44100f, Float.SIZE_BITS, 2, Float.SIZE_BYTES * frameSize, 44100f, true)
    } else {
        AudioFormat(44100f, Short.SIZE_BITS, 2, true, true)
    }
    private val line = AudioSystem.getSourceDataLine(format).also {
        it.open(format, frameSize)
    }

    override suspend fun loop() {
        logger.debug("Starting loop")

        line.start()
        val buffer = ByteArray(frameSize)
        while (true) {
            while (state != PlayerState.PLAYING) {
                delay(10L)
            }

            while (currentTrack == null || currentStream == null) {
                delay(100L)
            }

            val read = currentStream!!.copyInto(buffer, 0, buffer.size)

            if (read == -1) {
                logger.debug("End of stream, moving to next track")
                nextTrack()
            } else {
                line.write(buffer, 0, read)
            }
        }
    }

    override suspend fun pause() {
        super.pause()

    }

    // TODO: Implement MPRIS on Linux
}
