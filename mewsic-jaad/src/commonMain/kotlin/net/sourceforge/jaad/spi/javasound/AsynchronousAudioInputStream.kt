package net.sourceforge.jaad.spi.javasound
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import net.sourceforge.jaad.spi.javasound.CircularBuffer.Trigger

internal abstract class AsynchronousAudioInputStream(
    `in`: org.mewsic.commons.streams.api.InputStream?,
    length: Long
) : /* AudioInputStream(`in`, format, length), */ Trigger {
    private var singleByte: ByteArray? = null
    protected val buffer: CircularBuffer

    init {
        buffer = CircularBuffer(this)
    }

    @Throws(Exception::class)
    fun read(): Int {
        var i = -1
        if (singleByte == null) singleByte = ByteArray(1)
        i = if (buffer.read(singleByte!!, 0, 1) == -1) -1 else singleByte!![0].toInt() and 0xFF
        return i
    }

    @Throws(Exception::class)
    fun read(b: ByteArray): Int {
        return buffer.read(b, 0, b.size)
    }

    @Throws(Exception::class)
    fun read(b: ByteArray, off: Int, len: Int): Int {
        return buffer.read(b, off, len)
    }

    @Throws(Exception::class)
    fun skip(len: Long): Long {
        var l = len.toInt()
        val b = ByteArray(l)
        while (l > 0) {
            l -= buffer.read(b, 0, l)
        }
        return len
    }

    @Throws(Exception::class)
    fun available(): Int {
        return buffer.availableRead()
    }

    @Throws(Exception::class)
    fun close() {
        buffer.close()
    }

    fun markSupported(): Boolean {
        return false
    }

    fun mark(limit: Int) {}
    @Throws(Exception::class)
    fun reset() {
        throw Exception("mark not supported")
    }
}
