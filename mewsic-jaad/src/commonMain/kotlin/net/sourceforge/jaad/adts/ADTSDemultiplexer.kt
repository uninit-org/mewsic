package net.sourceforge.jaad.adts

import org.mewsic.commons.streams.DataInputStream
import org.mewsic.commons.streams.api.InputStream
import org.mewsic.commons.streams.api.SeekableInputStream

class ADTSDemultiplexer(private val `in`: SeekableInputStream) {
    private val din: DataInputStream
    private var first = true
    private var frame: ADTSFrame? = null

    init {

        din =DataInputStream(this.`in`)
        if (!findNextFrame()) throw Exception("no ADTS frame found")
    }

    val decoderSpecificInfo: ByteArray
        get() = frame!!.createDecoderSpecificInfo()

    @Throws(Exception::class)
    fun readNextFrame(): ByteArray {
        if (first) first = false else findNextFrame()
        val b = ByteArray(frame!!.getFrameLength())
        din.read(b)
        return b
    }

    @Throws(Exception::class)
    private fun findNextFrame(): Boolean {
        //find next ADTS ID
        var found = false
        var left = MAXIMUM_FRAME_SIZE
        var i: Int
        while (!found && left > 0) {
            i = `in`.read().toInt()
            left--
            if (i == 0xFF) {
                i = `in`.read().toInt()
                if (i and 0xF6 == 0xF0) found = true
                `in`.skip((-i).toLong())
            }
        }
        if (found) frame = net.sourceforge.jaad.adts.ADTSFrame(din)
        return found
    }

    val sampleFrequency: Int
        get() = frame!!.getSampleFrequency()
    val channelCount: Int
        get() = frame!!.channelCount

    companion object {
        private const val MAXIMUM_FRAME_SIZE = 6144
    }
}
