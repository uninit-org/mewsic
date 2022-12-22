package org.mewsic.commons.streams

import org.mewsic.commons.streams.api.InputStream

class SequenceInputStream(sequence: Sequence<ByteArray>) : InputStream {
    private val iterator = sequence.iterator()
    private var current: ByteArray? = null
    private var currentOffset = 0
    private var isOpen = true

    private fun getNextIfRequired() {
        if (!isOpen) {
            throw EndOfStreamException()
        }

        if (current == null || currentOffset >= current!!.size) {
            if (iterator.hasNext()) {
                current = iterator.next()
                currentOffset = 0
            } else {
                isOpen = false
                throw EndOfStreamException()
            }
        }
    }

    override fun read(): Byte {
        getNextIfRequired()
        return current!![currentOffset++]
    }

    override fun read(bytes: ByteArray): Int {
        val length = bytes.size
        var read = 0
        while (read < length) {
            getNextIfRequired()
            if (current == null) {
                break
            }
            val remaining = current!!.size - currentOffset
            val toRead = if (length - read > remaining) remaining else length - read
            current!!.copyInto(bytes, read, currentOffset, currentOffset + toRead)
            currentOffset += toRead
            read += toRead
        }
        return read
    }

    override fun read(bytes: ByteArray, offset: Int, length: Int): Int {
        var read = 0
        while (read < length) {
            getNextIfRequired()
            if (current == null) {
                break
            }
            val remaining = current!!.size - currentOffset
            val toRead = if (length - read > remaining) remaining else length - read
            current!!.copyInto(bytes, offset + read, currentOffset, currentOffset + toRead)
            currentOffset += toRead
            read += toRead
        }
        return read
    }

    override fun readNBytes(n: Int): ByteArray {
        val array = ByteArray(n)
        read(array)
        return array
    }

    override fun skip(n: Long): Long {
        var skipped = 0L
        while (skipped < n) {
            getNextIfRequired()
            if (current == null) {
                break
            }
            val remaining = (current!!.size - currentOffset).toLong()
            val toSkip = if (n - skipped > remaining) remaining else n - skipped
            currentOffset += toSkip.toInt()
            skipped += toSkip
        }
        return skipped
    }
}
