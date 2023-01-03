package org.mewsic.jaad.spi.javasound

import org.mewsic.jaad.aac.Decoder
import org.mewsic.jaad.aac.SampleBuffer
import org.mewsic.jaad.mp4.MP4Container
import org.mewsic.jaad.mp4.api.AudioTrack
import org.mewsic.jaad.mp4.api.Frame
import org.mewsic.jaad.mp4.api.Movie
import org.mewsic.jaad.mp4.api.Track

internal class MP4AudioInputStream(`in`: org.mewsic.commons.streams.api.InputStream, length: Long) :
    AsynchronousAudioInputStream(`in`, length) {
    private val track: AudioTrack
    private val decoder: Decoder
    private val sampleBuffer: SampleBuffer
    private var saved: ByteArray? = null

    init {
        val cont = MP4Container(`in`)
        val movie: Movie? = cont.getMovie()
        if (movie == null) {
            throw Exception("no movie found")
        }
        val tracks: List<Track> = movie.getTracks(AudioTrack.AudioCodec.AAC)
        if (tracks.isEmpty()) throw Exception(ERROR_MESSAGE_AAC_TRACK_NOT_FOUND)
        track = tracks[0] as AudioTrack
        decoder = Decoder(track.getDecoderSpecificInfo())
        sampleBuffer = SampleBuffer()
    }

//    val format: javax.sound.sampled.AudioFormat
//        get() {
//            if (audioFormat == null) {
//                //read first frame
//                decodeFrame()
//                audioFormat = javax.sound.sampled.AudioFormat(
//                    sampleBuffer.sampleRate.toFloat(),
//                    sampleBuffer.bitsPerSample,
//                    sampleBuffer.channels,
//                    true,
//                    true
//                )
//                saved = sampleBuffer.data
//            }
//            return audioFormat
//        }

    override fun execute() {
        if (saved == null) {
            decodeFrame()
            if (buffer.isOpen) buffer.write(sampleBuffer.data)
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
            val frame: Frame? = track.readNextFrame()
            if (frame == null) {
                buffer.close()
                return
            }
            decoder.decodeFrame(frame.data, sampleBuffer)
        } catch (e: Exception) {
            buffer.close()
            return
        }
    }

    companion object {
        const val ERROR_MESSAGE_AAC_TRACK_NOT_FOUND = "movie does not contain any AAC track"
    }
}
