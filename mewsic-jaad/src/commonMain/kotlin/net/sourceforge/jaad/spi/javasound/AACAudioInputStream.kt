package net.sourceforge.jaad.spi.javasound

import net.sourceforge.jaad.aac.Decoder
import net.sourceforge.jaad.adts.ADTSDemultiplexer

internal class AACAudioInputStream(`in`: java.io.InputStream?, format: javax.sound.sampled.AudioFormat?, length: Long) :
    net.sourceforge.jaad.spi.javasound.AsynchronousAudioInputStream(`in`, format, length) {
    private val adts: ADTSDemultiplexer
    private val decoder: Decoder
    private val sampleBuffer: SampleBuffer
    private var audioFormat: javax.sound.sampled.AudioFormat? = null
    private var saved: ByteArray?

    init {
        adts = ADTSDemultiplexer(`in`)
        decoder = Decoder(adts.getDecoderSpecificInfo())
        sampleBuffer = SampleBuffer()
    }

    val format: javax.sound.sampled.AudioFormat?
        get() {
            if (audioFormat == null) {
                //read first frame
                try {
                    decoder.decodeFrame(adts.readNextFrame(), sampleBuffer)
                    audioFormat = javax.sound.sampled.AudioFormat(
                        sampleBuffer.sampleRate.toFloat(),
                        sampleBuffer.bitsPerSample,
                        sampleBuffer.channels,
                        true,
                        true
                    )
                    saved = sampleBuffer.data
                } catch (e: java.io.IOException) {
                    return null
                }
            }
            return audioFormat
        }

    override fun execute() {
        try {
            if (saved == null) {
                decoder.decodeFrame(adts.readNextFrame(), sampleBuffer)
                buffer.write(sampleBuffer.data)
            } else {
                buffer.write(saved!!)
                saved = null
            }
        } catch (e: java.io.IOException) {
            buffer.close()
            return
        }
    }
}
