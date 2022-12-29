package net.sourceforge.jaad.adts

class ADTSDemultiplexer(`in`: java.io.InputStream?) {
    private val `in`: java.io.PushbackInputStream
    private val din: java.io.DataInputStream
    private var first = true
    private var frame: net.sourceforge.jaad.adts.ADTSFrame? = null

    init {
        this.`in` = java.io.PushbackInputStream(`in`)
        din = java.io.DataInputStream(this.`in`)
        if (!findNextFrame()) throw java.io.IOException("no ADTS header found")
    }

    val decoderSpecificInfo: ByteArray
        get() = frame!!.createDecoderSpecificInfo()

    @Throws(java.io.IOException::class)
    fun readNextFrame(): ByteArray {
        if (first) first = false else findNextFrame()
        val b = ByteArray(frame!!.getFrameLength())
        din.readFully(b)
        return b
    }

    @Throws(java.io.IOException::class)
    private fun findNextFrame(): Boolean {
        //find next ADTS ID
        var found = false
        var left = MAXIMUM_FRAME_SIZE
        var i: Int
        while (!found && left > 0) {
            i = `in`.read()
            left--
            if (i == 0xFF) {
                i = `in`.read()
                if (i and 0xF6 == 0xF0) found = true
                `in`.unread(i)
            }
        }
        if (found) frame = net.sourceforge.jaad.adts.ADTSFrame(din)
        return found
    }

    val sampleFrequency: Int
        get() = frame!!.getSampleFrequency()
    val channelCount: Int
        get() = frame.getChannelCount()

    companion object {
        private const val MAXIMUM_FRAME_SIZE = 6144
    }
}
