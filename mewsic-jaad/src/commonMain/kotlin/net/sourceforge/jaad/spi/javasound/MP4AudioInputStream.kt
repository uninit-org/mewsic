package net.sourceforge.jaad.spi.javasound

import net.sourceforge.jaad.aac.Decoder
import net.sourceforge.jaad.mp4.MP4Container

internal class MP4AudioInputStream(`in`: java.io.InputStream?, format: javax.sound.sampled.AudioFormat?, length: Long) :
    net.sourceforge.jaad.spi.javasound.AsynchronousAudioInputStream(`in`, format, length) {
    private val track: AudioTrack
    private val decoder: Decoder
    private val sampleBuffer: SampleBuffer
    private var audioFormat: javax.sound.sampled.AudioFormat? = null
    private var saved: ByteArray?

    init {
        val cont = MP4Container(`in`)
        val movie: Movie = cont.getMovie()
        val tracks: List<Track> = movie.getTracks(AudioTrack.AudioCodec.AAC)
        if (tracks.isEmpty()) throw java.io.IOException(ERROR_MESSAGE_AAC_TRACK_NOT_FOUND)
        track = tracks[0] as AudioTrack
        decoder = Decoder(track.getDecoderSpecificInfo())
        sampleBuffer = SampleBuffer()
    }

    val format: javax.sound.sampled.AudioFormat
        get() {
            if (audioFormat == null) {
                //read first frame
                decodeFrame()
                audioFormat = javax.sound.sampled.AudioFormat(
                    sampleBuffer.sampleRate.toFloat(),
                    sampleBuffer.bitsPerSample,
                    sampleBuffer.channels,
                    true,
                    true
                )
                saved = sampleBuffer.data
            }
            return audioFormat
        }

    override fun execute() {
        if (saved == null) {
            decodeFrame()
            if (buffer.isOpen()) buffer.write(sampleBuffer.data)
        } else {
            buffer.write(saved!!)
            saved = null
        }
    }

    private fun decodeFrame() {
        if (!track.hasMoreFrames()) {
            buffer.close()
            return
        }
        try {
            val frame: Frame = track.readNextFrame()
            if (frame == null) {
                buffer.close()
                return
            }
            decoder.decodeFrame(frame.getData(), sampleBuffer)
        } catch (e: java.io.IOException) {
            buffer.close()
            return
        }
    }

    companion object {
        const val ERROR_MESSAGE_AAC_TRACK_NOT_FOUND = "movie does not contain any AAC track"
    }
}
