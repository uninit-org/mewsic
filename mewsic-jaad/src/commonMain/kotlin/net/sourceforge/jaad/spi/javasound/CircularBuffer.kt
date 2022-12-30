package net.sourceforge.jaad.spi.javasound
import org.mewsic.commons.lang.Arrays

import org.mewsic.commons.streams.api.OutputStream
import org.mewsic.commons.streams.api.InputStream
import kotlin.jvm.JvmOverloads

/**
 * CircularBuffer for asynchronous reading.
 * Adopted from Tritonus (http://www.tritonus.org/).
 * @author in-somnia
 */
internal class CircularBuffer(private val trigger: Trigger?) {
    private val data: ByteArray
    private var readPos: Long = 0
    private var writePos: Long = 0
    var isOpen = true
        private set

    init {
        data = ByteArray(BUFFER_SIZE)
    }

    fun close() {
        isOpen = false
    }

    fun availableRead(): Int {
        return (writePos - readPos).toInt()
    }

    fun availableWrite(): Int {
        return BUFFER_SIZE - availableRead()
    }

    private fun getReadPos(): Int {
        return (readPos % BUFFER_SIZE).toInt()
    }

    private fun getWritePos(): Int {
        return (writePos % BUFFER_SIZE).toInt()
    }

    @JvmOverloads
    fun read(b: ByteArray, off: Int = 0, len: Int = b.length): Int {
        var off = off
        var len = len
        if (!isOpen) {
            len = if (availableRead() > 0) java.lang.Math.min(len, availableRead()) else return -1
        }
        synchronized(this) {
            if (trigger != null && availableRead() < len) {
                trigger.execute()
            }
            len = java.lang.Math.min(availableRead(), len)
            var remaining = len
            while (remaining > 0) {
                while (availableRead() == 0) {
                    try {
                        wait()
                    } catch (e: java.lang.InterruptedException) {
                    }
                }
                var available: Int = java.lang.Math.min(availableRead(), remaining)
                var toRead: Int
                while (available > 0) {
                    toRead = java.lang.Math.min(available, BUFFER_SIZE - getReadPos())
                    Arrays.arraycopy(data, getReadPos(), b, off, toRead)
                    readPos += toRead.toLong()
                    off += toRead
                    available -= toRead
                    remaining -= toRead
                }
                notifyAll()
            }
            return len
        }
    }

    @JvmOverloads
    fun write(b: ByteArray, off: Int = 0, len: Int = b.length): Int {
        var off = off
        synchronized(this) {
            var remaining = len
            while (remaining > 0) {
                while (availableWrite() == 0) {
                    try {
                        wait()
                    } catch (e: java.lang.InterruptedException) {
                    }
                }
                var available: Int = java.lang.Math.min(availableWrite(), remaining)
                var toWrite: Int
                while (available > 0) {
                    toWrite = java.lang.Math.min(available, BUFFER_SIZE - getWritePos())
                    Arrays.arraycopy(b, off, data, getWritePos(), toWrite)
                    writePos += toWrite.toLong()
                    off += toWrite
                    available -= toWrite
                    remaining -= toWrite
                }
                notifyAll()
            }
            return len
        }
    }

    internal interface Trigger {
        fun execute()
    }

    companion object {
        private const val BUFFER_SIZE = 327670
    }
}
