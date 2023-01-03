package net.sourceforge.jaad.spi.javasound

import net.sourceforge.jaad.aac.Decoder
import net.sourceforge.jaad.aac.SampleBuffer
import net.sourceforge.jaad.adts.ADTSDemultiplexer
import org.mewsic.commons.streams.api.SeekableInputStream

internal class AACAudioInputStream(val inputStream: SeekableInputStream, length: Long) :
    AsynchronousAudioInputStream(inputStream, length) {
    private val adts: ADTSDemultiplexer = ADTSDemultiplexer(inputStream)
    private val decoder: Decoder = Decoder(adts.decoderSpecificInfo)
    private val sampleBuffer: SampleBuffer
    private var saved: ByteArray? = null

    init {
        sampleBuffer = SampleBuffer()
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
        } catch (e: Exception) {
            buffer.close()
            return
        }
    }
}
