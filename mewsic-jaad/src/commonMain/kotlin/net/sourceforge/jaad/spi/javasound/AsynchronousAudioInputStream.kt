package net.sourceforge.jaad.spi.javasound

import net.sourceforge.jaad.spi.javasound.CircularBuffer.Trigger

internal abstract class AsynchronousAudioInputStream(
    `in`: java.io.InputStream?,
    format: javax.sound.sampled.AudioFormat?,
    length: Long
) : AudioInputStream(`in`, format, length), Trigger {
    private var singleByte: ByteArray?
    protected val buffer: net.sourceforge.jaad.spi.javasound.CircularBuffer

    init {
        buffer = net.sourceforge.jaad.spi.javasound.CircularBuffer(this)
    }

    @Throws(java.io.IOException::class)
    override fun read(): Int {
        var i = -1
        if (singleByte == null) singleByte = ByteArray(1)
        i = if (buffer.read(singleByte!!, 0, 1) == -1) -1 else singleByte!![0].toInt() and 0xFF
        return i
    }

    @Throws(java.io.IOException::class)
    override fun read(b: ByteArray): Int {
        return buffer.read(b, 0, b.size)
    }

    @Throws(java.io.IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return buffer.read(b, off, len)
    }

    @Throws(java.io.IOException::class)
    override fun skip(len: Long): Long {
        var l = len.toInt()
        val b = ByteArray(l)
        while (l > 0) {
            l -= buffer.read(b, 0, l)
        }
        return len
    }

    @Throws(java.io.IOException::class)
    override fun available(): Int {
        return buffer.availableRead()
    }

    @Throws(java.io.IOException::class)
    override fun close() {
        buffer.close()
    }

    override fun markSupported(): Boolean {
        return false
    }

    override fun mark(limit: Int) {}
    @Throws(java.io.IOException::class)
    override fun reset() {
        throw java.io.IOException("mark not supported")
    }
}
